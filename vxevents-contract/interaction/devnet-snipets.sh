CURRENT_ENV="devnet"
PROJECT="${PWD}"

cp -f mxpy.data-storage-devnet.json mxpy.data-storage.json
PEM_FILE="${PROJECT}/utils/simwalets/aliceWallet.pem"
PROXY=https://devnet-gateway.multiversx.com
CHAINID=D

TOKEN_USDT="USDT-58d5d0"
TOKEN_USDT_PERCENTAGE=35000

source interaction/contract-snipets.sh
