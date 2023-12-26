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
  }, [vxToken]);

  useEffect(() => {
    // Check if there is a token in localStorage
    const storedToken = localStorage.getItem("vxToken");

    // Set the token in state if it exists, otherwise set it to null
    setVxToken(storedToken || null);

    // Log the token value during initialization (optional)
    console.log("Token during initialization:", storedToken);
  }, []);

  return (
    <AuthContext.Provider
      value={{ user, googleSignIn, logOut, vxToken, setVxToken }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const UserAuth = () => {
  return useContext(AuthContext);
};
