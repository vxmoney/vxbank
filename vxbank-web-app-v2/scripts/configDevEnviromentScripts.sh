initEnvDevelopment() {
    cp scripts/dev-firebase.js src/app/firebase.js
    echo "Curent env = development"
}

deployOnDev(){
    initEnvDevelopment
    gcloud config set project vxbank-eu-dev
    gcloud config get-value project
    npm run build
    gcloud app deploy --quiet
}
