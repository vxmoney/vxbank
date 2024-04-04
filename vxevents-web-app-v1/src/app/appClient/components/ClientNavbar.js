import Link from "next/link";
import { useEffect, useState } from "react";
import { UserAuth } from "@/app/context/AuthContext";
import ThemeSwitch from "@/app/components/ThemeSwitch";
import { useParams } from "next/navigation";

export default function ClientNavbar() {
  const { googleSignIn, logOut, vxUserInfo, pendingLogin } = UserAuth();
  let { eventId } = useParams();

  const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    setIsClient(true);
  }, []);

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

  let loginButtonContent = (
    <li onClick={handleSignIn} className="p-2 cursor-pointer">
      Login
    </li>
  );

  if (pendingLogin) {
    loginButtonContent = <li>Logging in ... </li>;
  } else if (vxUserInfo) {
    loginButtonContent = (
      <>
        <li onClick={handleSignOut} className="p-2 cursor-pointer">
          Logout
        </li>
      </>
    );
  }

  return (
    <div className="h-20 w-full border-b-2 flex items-center justify-between p-2 bg-light dark:bg-gray-800">
      {vxUserInfo && (
        <ul className="flex">
          <li className="p-2 cursor-pointer">
            <Link href={`/appClient/account`}>Account</Link>
          </li>
        </ul>
      )}
      <ul className="flex">
        {isClient && loginButtonContent}
        <ThemeSwitch />
      </ul>
    </div>
  );
}
