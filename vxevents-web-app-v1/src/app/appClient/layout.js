"use client";
import ClientNavbar from "./components/ClientNavbar";

export default function ClientLayout({ children }) {
  return (
    <>
      <ClientNavbar />
      {children}
    </>
  );
}
