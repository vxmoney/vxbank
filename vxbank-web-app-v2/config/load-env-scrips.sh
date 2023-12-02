setStgEnv() {
    cp config/stg-firebase.js src/app/firebase.js
    echo "Curent env = staging"
}

setDevEnv() {
    cp config/dev-firebase.js src/app/firebase.js
    echo "Curent env = development"
}
