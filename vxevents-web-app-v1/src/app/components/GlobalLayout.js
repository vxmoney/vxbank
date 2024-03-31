"use client";
import { AuthContextProvider } from "../context/AuthContext";
import { VxProvider } from "../context/VxContext";
import Navbar from "./Navbar";
import { MyThemeProvider } from "../context/MyThemeContext";

export default function GlobalLayout({ children }) {
  return (
    <AuthContextProvider>
      <VxProvider>
        <MyThemeProvider>{children}</MyThemeProvider>
      </VxProvider>
    </AuthContextProvider>
  );
}
