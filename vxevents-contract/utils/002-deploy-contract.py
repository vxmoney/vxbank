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
    print("Start deploy contract")
    provider = ProxyNetworkProvider(SIMULATOR_URL)

    pem = UserPEM.from_file(Path("./utils/simwalets/aliceWallet.pem"))

    address = pem.public_key.to_address("erd")
    data = {"receiver": f"{address.to_bech32()}"}
    provider.do_post(f"{SIMULATOR_URL}/transaction/send-user-funds", data)

    provider.do_post(f"{GENERATE_BLOCKS_URL}/20", {})

    config = TransactionsFactoryConfig(provider.get_network_config().chain_id)

    sc_factory = SmartContractTransactionsFactory(config, TokenComputer())

    bytecode = Path("output/vx-events.wasm").read_bytes()
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

    time.sleep(1)

    provider.do_post(f"{GENERATE_BLOCKS_URL}/1", {})

    tx_from_network = provider.get_transaction(tx_hash, with_process_status=True)
    print(repr(tx_from_network))
    if not tx_from_network.status.is_successful():
        sys.exit(f"Transaction status is not correct, status received: {tx_from_network.status}")

    print("End deploy contract")


def extract_contract_address(tx: TransactionOnNetwork) -> Address:
    factory = AddressFactory("erd")
    for event in tx.logs.events:
        if event.identifier != "SCDeploy":
            continue

        return factory.create_from_hex(event.topics[0].hex())


if __name__ == "__main__":
    main()
