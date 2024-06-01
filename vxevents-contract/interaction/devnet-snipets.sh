CURRENT_ENV="devnet"
PROJECT="${PWD}"

cp -f mxpy.data-storage-devnet.json mxpy.data-storage.json
PEM_FILE="${PROJECT}/utils/simwalets/aliceWallet.pem"
PROXY=https://devnet-gateway.multiversx.com
CHAINID=D

source interaction/contract-snipets.sh
