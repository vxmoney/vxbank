
MY_DECIMALS="000000000000000000"
MY_BYTECODE="output/vx-events.wasm"
CORE_LOGS="interaction/logs"
ENV_LOGS="${CORE_LOGS}/${CURRENT_ENV}"

TOKEN_USDT_HEX=$(echo -n ${TOKEN_USDT} | xxd -p)

ADDRESS=$(mxpy data load --key=address-devnet)

setEnvDevnet() {
  CURRENT_ENV="devnet"
  ENV_LOGS="${CORE_LOGS}/${CURRENT_ENV}"
  cp -f mxpy.data-storage-devnet.json mxpy.data-storage.json
  PEM_FILE="${PROJECT}/utils/simwalets/aliceWallet.pem"
  PROXY=https://devnet-gateway.multiversx.com
  CHAINID=D
}

deployContract() {
  MY_LOGS="${ENV_LOGS}-deploy.json"
  mxpy --verbose contract deploy --bytecode ${MY_BYTECODE} --recall-nonce --pem=${PEM_FILE} \
    --gas-limit=100000000 --send --outfile="${MY_LOGS}" \
    --proxy=${PROXY} --chain=${CHAINID} || return

  TRANSACTION=$(mxpy data parse --file="${MY_LOGS}" --expression="data['emitted_tx']['hash']")
  ADDRESS=$(mxpy data parse --file="${MY_LOGS}" --expression="data['emitted_tx']['address']")

  mxpy data store --key=address-devnet --value=${ADDRESS}
  mxpy data store --key=deployTransaction-devnet --value=${TRANSACTION}

  echo ""
  echo "Smart contract address: ${ADDRESS}"
}

setUsdtProcessingPercentage(){
  MY_LOGS="${ENV_LOGS}-setTokenProcessingPercentage.json"
  mxpy --verbose contract call ${ADDRESS} --recall-nonce \
      --pem=${PEM_FILE} \
      --gas-limit=8000000 \
      --proxy=${PROXY} --chain=${CHAINID} \
      --function="setTokenProcessingPercentage" \
      --arguments "0x${TOKEN_USDT_HEX}" ${TOKEN_USDT_PERCENTAGE} \
      --send \
      --outfile="${MY_LOGS}"
}