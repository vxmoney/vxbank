
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

startDatastoreEmulator(){
  gcloud beta emulators datastore start --project=my-project-id --no-store-on-disk --consistency 1.0
}

startOauthEmulator(){
  cd ${OAUTH_DIR}
  firebase emulators:start
}

oauthLogin(){
  gcloud auth login
  cd ${OAUTH_DIR}
  firebase login:use bogdan.oloeriu@gmail.com
}

setEnvAndStartIntellij(){
  export GOOGLE_APPLICATION_CREDENTIALS="/home/bogdan/workspace/vxbank/vxbank-api/src/main/resources/vxbank-eu-dev-key.json"
  export FIREBASE_AUTH_EMULATOR_HOST="127.0.0.1:9099"
  nohup intellij-idea-community .
}