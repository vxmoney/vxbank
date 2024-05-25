import base64
import sys
import time
from pathlib import Path

from multiversx_sdk_core import TokenComputer, TransactionComputer, AddressFactory, Address, ContractQueryBuilder
from multiversx_sdk_core.transaction_factories import SmartContractTransactionsFactory, TransactionsFactoryConfig
from multiversx_sdk_network_providers import ProxyNetworkProvider
from multiversx_sdk_network_providers.transactions import TransactionOnNetwork
from multiversx_sdk_wallet import UserPEM, UserSigner

SIMULATOR_URL = "http://localhost:8085"
GENERATE_BLOCKS_URL = f"{SIMULATOR_URL}/simulator/generate-blocks"

def main():
    provider = ProxyNetworkProvider(SIMULATOR_URL)

    pem = UserPEM.from_file(Path("./wallet.pem"))

    address = pem.public_key.to_address("erd")
    data = {"receiver": f"{address.to_bech32()}"}
    provider.do_post(f"{SIMULATOR_URL}/transaction/send-user-funds", data)

    provider.do_post(f"{GENERATE_BLOCKS_URL}/20", {})

    config = TransactionsFactoryConfig(provider.get_network_config().chain_id)

    sc_factory = SmartContractTransactionsFactory(config, TokenComputer())

    bytecode = Path("./adder.wasm").read_bytes()
    deploy_transaction = sc_factory.create_transaction_for_deploy(
        sender=address,
        bytecode=bytecode,
        arguments=[0],
        gas_limit=10000000,
        is_upgradeable=True,
        is_readable=True,
        is_payable=True,
        is_payable_by_sc=True
    )

    deploy_transaction.nonce = provider.get_account(address).nonce

    user_signer = UserSigner(pem.secret_key)
    tx_computer = TransactionComputer()
    deploy_transaction.signature = user_signer.sign(tx_computer.compute_bytes_for_signing(deploy_transaction))

    tx_hash = provider.send_transaction(deploy_transaction)
    print(f"Deploy transaction hash: {tx_hash}")

    time.sleep(0.5)

    provider.do_post(f"{GENERATE_BLOCKS_URL}/1", {})

    tx_from_network = provider.get_transaction(tx_hash, with_process_status=True)

    if not tx_from_network.status.is_successful():
        sys.exit(f"Transaction status is not correct, status received: {tx_from_network.status}")

    value = 10
    contract_address = extract_contract_address(tx_from_network)
    call_transaction = sc_factory.create_transaction_for_execute(
        sender=address,
        contract=contract_address,
        function="add",
        gas_limit=10000000,
        arguments=[value]
    )

    call_transaction.nonce = provider.get_account(address).nonce
    call_transaction.signature = user_signer.sign(tx_computer.compute_bytes_for_signing(call_transaction))

    tx_hash = provider.send_transaction(call_transaction)
    print(f"Smart contract call transaction hash: {tx_hash}")

    time.sleep(0.5)

    provider.do_post(f"{GENERATE_BLOCKS_URL}/1", {})

    builder = ContractQueryBuilder(
        contract=contract_address,
        function="getSum",
        call_arguments=[],
        caller=address
    )
    query = builder.build()
    response = provider.query_contract(query)
    decoded_bytes = base64.b64decode(response.return_data[0])

    result_int = int.from_bytes(decoded_bytes, byteorder='big')
    print("Value:", result_int)
    if value != result_int:
        sys.exit(f"Value from VM query is wrong, expected: {value}, received: {result_int}")

def extract_contract_address(tx: TransactionOnNetwork) -> Address:
    factory = AddressFactory("erd")
    for event in tx.logs.events:
        if event.identifier != "SCDeploy":
            continue

        return factory.create_from_hex(event.topics[0].hex())

if __name__ == "__main__":
    main()
