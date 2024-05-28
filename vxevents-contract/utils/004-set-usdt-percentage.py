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


def load_value_from_file(filename):
    try:
        with open(filename, 'r') as file:
            value = file.read().strip()
            return value
    except FileNotFoundError:
        print(f"The file {filename} does not exist.")
        return None
    except Exception as e:
        print(f"An error occurred: {e}")
        return None


def check_transaction_status(provider, tx_hash, expected_status):
    try:
        transaction = provider.get_transaction(tx_hash)
        actual_status = transaction.status
        print(f"Transaction status: {actual_status}")
        if actual_status != expected_status:
            print(f"Transaction status does not match the expected status: {expected_status}")
            sys.exit(1)
        else:
            print("Transaction status matches the expected status.")
    except Exception as e:
        print(f"An error occurred while fetching the transaction: {e}")
        sys.exit(1)


def main():
    provider = ProxyNetworkProvider(SIMULATOR_URL)
    ownerPem = UserPEM.from_file(Path("./utils/simwalets/aliceWallet.pem"))
    ownerAddress = ownerPem.public_key.to_address("erd")

    config = TransactionsFactoryConfig(provider.get_network_config().chain_id)
    sc_factory = SmartContractTransactionsFactory(config, TokenComputer())

    provider.do_post(f"{GENERATE_BLOCKS_URL}/1", {})

    contract_address_B32 = load_value_from_file("./utils/contract_address.txt")
    token_identifier_B32 = load_value_from_file("./utils/token_identifier.txt")
    token_percentage = 10000
    print(f"loaded contract_address_B32 = {contract_address_B32}")
    print(f"loaded token_identifier = {token_identifier_B32}")

    contract_address = Address.new_from_bech32(contract_address_B32)
    bad_call_transaction = sc_factory.create_transaction_for_execute(
        sender=ownerAddress,
        contract=contract_address,
        function="badEndpoint",
        gas_limit=10000000,
        arguments=["fake-token", token_percentage]
    )
    bad_call_transaction.nonce = provider.get_account(ownerAddress).nonce
    user_signer = UserSigner(ownerPem.secret_key)
    tx_computer = TransactionComputer()
    bad_call_transaction.signature = user_signer.sign(tx_computer.compute_bytes_for_signing(bad_call_transaction))

    # send transaction
    tx_hash = provider.send_transaction(bad_call_transaction)
    print(f"sc call tx hash: {tx_hash}")

    # execute 1 block
    time.sleep(0.5)
    provider.do_post(f"{GENERATE_BLOCKS_URL}/1", {})

    # Check transaction status
    check_transaction_status(provider, tx_hash, expected_status="fail")

    print("Hello main")


if __name__ == "__main__":
    main()
