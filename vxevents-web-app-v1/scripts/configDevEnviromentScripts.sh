initEnvDevelopment() {
    cp scripts/dev-firebase.js src/app/firebase.js
    cp scripts/dev-apiConfig.js src/api/apiConfig.js
    echo "Curent env = development"
}

initEnvProduction() {
    cp scripts/prod-firebase.js src/app/firebase.js
    cp scripts/prod-apiConfig.js src/api/apiConfig.js
    echo "Curent env = production"
}

initEnvLocalhost() {
    cp scripts/dev-firebase.js src/app/firebase.js
    cp scripts/localhost-apiConfig.js src/api/apiConfig.js
    echo "Curent env = localhost"
}

initEnvBogdanHome(){
    cp scripts/dev-firebase.js src/app/firebase.js
    cp scripts/bogdanHome-apiConfig.js src/api/apiConfig.js
    echo "Curent env = BogdanHome"
}

deployOnDev() {
    initEnvDevelopment
    gcloud config set project vxbank-eu-dev
    gcloud config get-value project
    npm run build
    gcloud app deploy --quiet
    initEnvLocalhost
}

deployOnProd() {
    initEnvProduction
    gcloud config set project vxbank-eu-prod
    gcloud config get-value project
    npm run build
    gcloud app deploy --quiet
    initEnvLocalhost
}
