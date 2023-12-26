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
  const [vxToken, setVxToken] = useState(() => {
    // Try to get the token from localStorage on component mount
    const storedToken = localStorage.getItem("vxToken");
    return storedToken || null;
  });
  
  const [vxUser, setVxUser] = useState(() => {
    // Try to get the token from localStorage on component mount
    const storedVxUser = localStorage.getItem("vxUser");
    return storedVxUser || null;
  });

  const googleSignIn = () => {
    const provider = new GoogleAuthProvider();
    signInWithPopup(auth, provider);
  };

  const logOut = () => {
    signOut(auth);
  };

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
      setUser(currentUser);
    });
    return () => unsubscribe();
  }, [user]);

  useEffect(() => {
    console.log("vxToken value = ", vxToken);
    localStorage.setItem("vxToken", vxToken);
  }, [vxToken]);

  useEffect(() => {
    localStorage.setItem("vxUser", vxUser);
  }, [vxUser]);

  useEffect(() => {
    const storedToken = localStorage.getItem("vxToken");
    setVxToken(storedToken || null);
  }, []);

  return (
    <AuthContext.Provider
      value={{ user, googleSignIn, logOut, vxToken, setVxToken , vxUser, setVxUser}}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const UserAuth = () => {
  return useContext(AuthContext);
};
