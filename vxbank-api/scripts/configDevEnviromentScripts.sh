function showHelp() {
  echo "showHelp                  Prints this help"
  echo "initDevEnvironment &      It initializes the development environment in background. You need to hit enter
                                  When you see that the logs are settled in order to get back the prompt"
  echo "stopDevEnvironment        It stops the development environment."

  echo "configElasticMaxMapCount  It configures some requyerd setting for elastic containers that are mandatory. You
                                  only need to run this once after system reboot"
  echo "installVxModules     Installs the current version of the prompto modules (prompto-graph, prompto-datastore)"
  echo ""

}

API_DIR=$(pwd)
API_KYES_DIR="${API_DIR}/../../vxbank-security"
OAUTH_DIR=emulators
DATA_DIR="${API_DIR}/datastore-generated"
VSCODE_DIR="${API_DIR}/../vxbank-web-app-v2"

startDatastoreEmulator() {

  # no store on disc
  gcloud beta emulators datastore start --project=my-project-id --no-store-on-disk --consistency 1.0 --host-port=localhost:8081 &

  # persisted on disk
  #  gcloud beta emulators datastore start --project=my-project-id --data-dir "${DATA_DIR}" --consistency 1.0 --host-port=localhost:8081 &

  EMULATOR_PID=$!
  echo "Emulator PID: $EMULATOR_PID"
}

stopDatastoreEmulator() {
  killProcessOnPort 8081
}

startOauthEmulator() {
  cd ${OAUTH_DIR}
  firebase emulators:start
}

oauthLogin() {
  gcloud auth login
  cd ${OAUTH_DIR}
  firebase login:use bogdan.oloeriu@gmail.com
}

killProcessOnPort() {
  MY_PORT=$1
  lsof -i tcp:$MY_PORT | awk 'NR!=1 {print $2}' | xargs kill
  echo "killed listeners on $MY_PORT"
}

openIntellij() {
  export GOOGLE_APPLICATION_CREDENTIALS="${HOME}/workspace/vxbank/vxbank-api/src/main/resources/vxbank-eu-dev-key.json"
  export FIREBASE_AUTH_EMULATOR_HOST="127.0.0.1:9099"
  initDevEnvironment &
  nohup intellij-idea-community .
}

openIdea() {
  export GOOGLE_APPLICATION_CREDENTIALS="${HOME}/workspace/vxbank/vxbank-api/src/main/resources/vxbank-eu-dev-key.json"
  export FIREBASE_AUTH_EMULATOR_HOST="127.0.0.1:9099"
  initDevEnvironment &
  nohup idea .
}

unsetVariables() {
  unset GOOGLE_APPLICATION_CREDENTIALS
  unset FIREBASE_AUTH_EMULATOR_HOST
}

openVsCode() {
  # nvm alias default 20
  unsetVariables
  cd ../vxbank-web-app-v2
  nohup code .
  exit
}

gcloudLogin() {
  firebase logout
  firebase login
  gcloud auth login
}

initDevEnvironment() {
  # gcloudLogin
  cd $API_KYES_DIR
  $(pwd)
  source load-security-commands.sh
  initSecurity
  cd $API_DIR
  startDatastoreEmulator &
  cd $API_DIR
  startOauthEmulator &
  cd $API_DIR
  echo "EMULATORS INITIATED"
}

stopDevEnvironment() {
  killProcessOnPort 4400
  killProcessOnPort 9099
  killProcessOnPort 4000
  killProcessOnPort 8081
}

# ping stuff

pingLocal() {
  curl localhost:8080/ping/getEnvironment
}

deployDatastoreIndexOnDev() {
  gcloud config set project vxbank-eu-dev
  gcloud datastore indexes create ./src/main/appengine/index.yaml
}

## remote debug
remoteDebugDev() {
  unsetVariables
  stopDevEnvironment
  cd $API_KYES_DIR
  source load-security-commands.sh
  initSecurity
  cd $API_DIR
  export GOOGLE_APPLICATION_CREDENTIALS="${HOME}/workspace/vxbank/vxbank-api/src/main/resources/vxbank-eu-dev-key.json"
  export GAE_APPLICATION="vxbank-eu-dev"

  # vscode part
  # we do all vscode configuration from this script
  # when remote debug you only need to start the npm run dev
  echo "vscode part"
  cd $VSCODE_DIR
  pwd
  echo "that was pwd"
  cp scripts/dev-firebase.js src/app/firebase.js
  cp scripts/remote-apiConfig.js src/api/apiConfig.js

  cd $API_DIR
  nohup intellij-idea-community .
}

remoteDebugProd() {
  unsetVariables
  stopDevEnvironment
  cd $API_KYES_DIR
  $(pwd)
  source load-security-commands.sh
  initSecurity
  cd $API_DIR
  export GOOGLE_APPLICATION_CREDENTIALS="${HOME}/workspace/vxbank/vxbank-api/src/main/resources/vxbank-eu-prod-key.json"
  export GAE_APPLICATION="vxbank-eu-prod"

    # vscode part
    # we do all vscode configuration from this script
    # when remote debug you only need to start the npm run dev
    echo "vscode part"
    cd $VSCODE_DIR
    pwd
    echo "that was pwd"
    cp scripts/prod-firebase.js src/app/firebase.js
    cp scripts/remote-apiConfig.js src/api/apiConfig.js

    cd $API_DIR
    nohup intellij-idea-community .

  nohup intellij-idea-community .
}

deployIndexDev(){
  gcloud config set project vxbank-eu-dev
  gcloud app deploy src/main/appengine/index.yaml
}

deployIndexProd(){
  gcloud config set project vxbank-eu-prod
  gcloud app deploy src/main/appengine/index.yaml
}
