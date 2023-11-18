// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import { getAuth } from "firebase/auth";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
    apiKey: "AIzaSyD3WNQhta9K8SW42PpGDnozZwm16vTJq8k",
    authDomain: "vxbank-eu-dev.firebaseapp.com",
    projectId: "vxbank-eu-dev",
    storageBucket: "vxbank-eu-dev.appspot.com",
    messagingSenderId: "762103373472",
    appId: "1:762103373472:web:4314f42476e42cf37073c5",
    measurementId: "G-PMLBWYSCB4"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
export const auth = getAuth(app);