import { useContext, createContext, useState, useEffect } from "react";
import {
  signInWithPopup,
  signOut,
  onAuthStateChanged,
  GoogleAuthProvider,
} from "firebase/auth";
import { auth } from "../firebase";

const AuthContext = createContext();

export const AuthContextProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [myFirebaseToken, setMyFirebaseToken] = useState(null);

  const googleSignIng = () => {
    const provider = new GoogleAuthProvider();
    signInWithPopup(auth, provider);
  };

  const logOut = () => {
    signOut(auth);
  };

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
      if (currentUser != null) {
        currentUser.getIdToken(true).then(function (idToken) {
          setMyFirebaseToken(idToken);
        });
        let inlineIdToken = currentUser.getIdToken(true);
      }
      setUser(currentUser);
    });
    return () => unsubscribe();
  }, [user]);

  useEffect(() => {
    console.log("DEBUG line2 firebase token = ", myFirebaseToken);
  },[myFirebaseToken]);

  return (
    <AuthContext.Provider value={{ user, googleSignIng, logOut }}>
      {children}
    </AuthContext.Provider>
  );
};

export const UserAuth = () => {
  return useContext(AuthContext);
};
