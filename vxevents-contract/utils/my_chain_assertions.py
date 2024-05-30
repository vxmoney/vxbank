from multiversx_sdk_core.transaction_status import TransactionStatus

def check_is_failed(provider, tx_hash):
    try:
        transaction = provider.get_transaction(tx_hash, True)
        actual_status = TransactionStatus(transaction.status.status)  # Adjust based on how status is accessed
        if not actual_status.is_failed():
            print(f"Transaction did not fail as expected. Status: {actual_status}")
            sys.exit(1)
        else:
            print("Transaction failed as expected.")
    except Exception as e:
        print(f"An error occurred while fetching the transaction: {e}")
        sys.exit(1)

def check_is_successful(provider, tx_hash):
    try:
        transaction = provider.get_transaction(tx_hash, True)
        actual_status = TransactionStatus(transaction.status.status)  # Adjust based on how status is accessed
        if not actual_status.is_successful():
            print(f"Transaction was not successful. Status: {actual_status}")
            sys.exit(1)
        else:
            print("Transaction was successful.")
    except Exception as e:
        print(f"An error occurred while fetching the transaction: {e}")
        sys.exit(1)
