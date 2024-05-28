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

def main():
    contract_address = load_value_from_file("./utils/contract_address.txt")
    token_identifier = load_value_from_file("./utils/token_identifier.txt")
    print(f"loaded contract_address = {contract_address}")
    print(f"loaded token_identifier = {token_identifier}")
    print("Hello main")

if __name__ == "__main__":
    main()
