set-env-stg() {
    cp config/stg-firebase.js src/app/firebase.js
    echo "Curent env = staging"
}

set-env-dev() {
    cp config/dev-firebase.js src/app/firebase.js
    echo "Curent env = development"
}

deploy-on-dev(){
    set-env-dev
    rm -rf build
    gcloud config set project vxbank-eu-dev
    gcloud config get-value project
    npm run build
    gcloud app deploy --quiet
}
