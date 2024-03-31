"use client";
import ManageNavbar from "./components/ManageNavbar";

export default function ClientLayout({ children }) {
  return (
    <>
      <ManageNavbar />
      {children}
    </>
  );
}
