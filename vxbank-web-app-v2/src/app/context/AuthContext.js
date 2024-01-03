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
    if (typeof localStorage !== "undefined") {
      const storedToken = localStorage.getItem("vxToken");
      return storedToken || null;
    } else {
      return null;
    }
  });

  const [vxUserInfo, setVxUserInfo] = useState(() => {
    if (typeof localStorage !== "undefined") {
      const storedVxUserInfo = localStorage.getItem("vxUserInfo");
      return storedVxUserInfo ? JSON.parse(storedVxUserInfo) : null;
    }else{
      return null;
    }
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
    localStorage.setItem("vxUserInfo", JSON.stringify(vxUserInfo));
  }, [vxUserInfo]);

  useEffect(() => {
    const storedToken = localStorage.getItem("vxToken");
    setVxToken(storedToken || null);
    const storedVxUserInfo = localStorage.getItem("vxUserInfo");
    // setVxUserInfo(storedVxUserInfo ? JSON.parse(storedVxUserInfo) : null);
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        googleSignIn,
        logOut,
        vxToken,
        setVxToken,
        vxUserInfo,
        setVxUserInfo,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const UserAuth = () => {
  return useContext(AuthContext);
};
