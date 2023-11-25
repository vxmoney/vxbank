"use client";
import { AuthContextProvider } from "../context/AuthContext";
import Navbar from "./Navbar";

export default function AppClientLayout({ children }) {
  return (
    <AuthContextProvider>
      <Navbar />
      {children}
    </AuthContextProvider>
  );
}
