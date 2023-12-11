initEnvDevelopment() {
    cp scripts/dev-firebase.js src/app/firebase.js
    cp scripts/dev-apiConfig.js src/api/apiConfig.js
    echo "Curent env = development"
}
initEnvLocalhost() {
    cp scripts/dev-firebase.js src/app/firebase.js
    cp scripts/localhost-apiConfig.js src/api/apiConfig.js
    echo "Curent env = localhost"
}

deployOnDev() {
    initEnvDevelopment
    gcloud config set project vxbank-eu-dev
    gcloud config get-value project
    npm run build
    gcloud app deploy --quiet
}
