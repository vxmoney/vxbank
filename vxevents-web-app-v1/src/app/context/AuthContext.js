import { useContext, createContext, useState, useEffect } from "react";
import {
  signInWithPopup,
  signOut,
  onAuthStateChanged,
  GoogleAuthProvider,
} from "firebase/auth";
import { userAPI } from "@/api/user";
import { auth } from "../firebase";

const AuthContext = createContext();

export const AuthContextProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [pendingLogin, setPendingLogin] = useState(false);
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
    } else {
      return null;
    }
  });

  const removeVxUserInfo = () => {
    setVxUserInfo(null);
    if (typeof localStorage !== "undefined") {
      localStorage.removeItem("vxUserInfo");
    }
  };

  const googleSignIn = () => {
    const provider = new GoogleAuthProvider();
    setPendingLogin(true);
    console.log(pendingLogin);

    // Sign in with Google and update local state when successful
    signInWithPopup(auth, provider)
      .then(async (result) => {
        const { user, credential } = result;
        setUser(user);
        console.log("AuthContext user", user);
        // Additional logic if needed
        const response = await userAPI.login(user.accessToken);
        setVxUserInfo(response.data);
        setPendingLogin(false);
      })
      .catch((error) => {
        console.error("Google Sign-In Error:", error);
        setPendingLogin(false);
      });
  };

  const logOut = () => {
    signOut(auth);
    removeVxUserInfo();
  };

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
      setUser(currentUser);
    });
    return () => unsubscribe();
  }, [user]);

  useEffect(() => {
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
        pendingLogin,
        user,
        googleSignIn,
        logOut,
        vxToken,
        setVxToken,
        vxUserInfo,
        setVxUserInfo,
        removeVxUserInfo,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const UserAuth = () => {
  return useContext(AuthContext);
};
