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
OAUTH_DIR=emulators

startDatastoreEmulator() {
  gcloud beta emulators datastore start --project=my-project-id --no-store-on-disk --consistency 1.0 --host-port=localhost:8081 &
  EMULATOR_PID=$!
  echo "Emulator PID: $EMULATOR_PID"
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
  export GOOGLE_APPLICATION_CREDENTIALS="/home/bogdan/workspace/vxbank/vxbank-api/src/main/resources/vxbank-eu-dev-key.json"
  export FIREBASE_AUTH_EMULATOR_HOST="127.0.0.1:9099"
  nohup intellij-idea-community .
}

gcloudLogin() {
  firebase logout
  firebase login
  gcloud auth login
}

initDevEnvironment() {
  gcloudLogin
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
