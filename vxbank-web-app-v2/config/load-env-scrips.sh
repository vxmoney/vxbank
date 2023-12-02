setStgEnv(){
    echo "Time to set development environment"
    echo $(pwd)
    cp config/stg-firebase.js src/app/firebase.js
}