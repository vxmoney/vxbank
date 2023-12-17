// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries
import { getAuth } from "firebase/auth";

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyAOvN4KqVt5A8SGDwCY2IHbiZv-fSdJ00M",
  authDomain: "vxbank-eu-prod.firebaseapp.com",
  projectId: "vxbank-eu-prod",
  storageBucket: "vxbank-eu-prod.appspot.com",
  messagingSenderId: "703299368858",
  appId: "1:703299368858:web:298d176b2a373804cc2c23",
  measurementId: "G-GD4Q2M5EBK"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

export const auth = getAuth(app);
