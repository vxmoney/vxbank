// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries
import { getAuth } from "firebase/auth";

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyCxLNr5iNjoSh0fsPeyy1qHxXgN-119xtA",
  authDomain: "mysandbox-v4.firebaseapp.com",
  projectId: "mysandbox-v4",
  storageBucket: "mysandbox-v4.appspot.com",
  messagingSenderId: "17218317034",
  appId: "1:17218317034:web:5e3bba782c3823e71a0593",
  measurementId: "G-75518ZLGHT"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
export const auth = getAuth(app);