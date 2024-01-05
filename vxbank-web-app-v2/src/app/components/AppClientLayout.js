"use client";
import { AuthContextProvider } from "../context/AuthContext";
import { VxProvider } from "../context/VxContext";
import Navbar from "./Navbar";

export default function AppClientLayout({ children }) {
  return (
    <AuthContextProvider>
      <VxProvider>
        <Navbar />
        {children}
      </VxProvider>
    </AuthContextProvider>
  );
}
