import Link from "next/link";
import { UserAuth } from "../context/AuthContext";

export default function Navbar() {
  const { user, googleSignIn, logOut } = UserAuth();

  console.log("Navbar user", user);
  const handleSignIn = async () => {
    console.log("Handle sign in");
    try {
      await googleSignIn();
    } catch (error) {
      console.log(error);
    }
  };

  const handleSignOut = async () => {
    try {
      await logOut();
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div className="h-20 w-full border-b-2 flex items-center justify-between p-2">
      <ul className="flex">
        <li className="p-2 cursor-pointer">
          <Link href="/">Home</Link>
        </li>
        <li className="p-2 cursor-pointer">
          <Link href="/about">About</Link>
        </li>
        <li className="p-2 cursor-pointer">
          <Link href="/profile">Profile</Link>
        </li>
        <li className="p-2 cursor-pointer">
          <Link href="/usageExamples">DeveloperExamples</Link>
        </li>
      </ul>
      <ul className="flex">
        {user ? (
          <li className="p-2 cursor-pointer">{user.displayName}</li>
        ) : (
          <li onClick={handleSignIn} className="p-2 cursor-pointer">
            Login
          </li>
        )}

        {user ? (
          <li onClick={handleSignOut} className="p-2 cursor-pointer">
            Logout
          </li>
        ) : null}
      </ul>
    </div>
  );
}
