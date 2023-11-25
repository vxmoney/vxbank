import Navbar from "./Navbar";

export default function AppClientLayout({ children }) {
  return (
    <>
      <Navbar /> {children}
    </>
  );
}
