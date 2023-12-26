import { useContext, createContext, useState, useEffect } from "react";
import { signInWithPopup, signOut, onAuthStateChanged, GoogleAuthProvider } from "firebase/auth";
import { auth } from "../firebase";

const AuthContext = createContext();

export const AuthContextProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [vxToken, setVxToken] = useState(null);

  const googleSignIn = () =>{
    const provider = new GoogleAuthProvider()
    signInWithPopup(auth, provider)
  }

  const logOut = () =>{
    signOut(auth)
  }

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (currentUser) =>{
        setUser(currentUser)
    })
    return () => unsubscribe()
  }, [user])

  useEffect (() => {
    console.log("vxToken value = ", vxToken)
  },[vxToken])

  return <AuthContext.Provider value={{user, googleSignIn, logOut, vxToken, setVxToken}}>{children}</AuthContext.Provider>;

};

export const UserAuth = () => {
  return useContext(AuthContext);
};
