
import Link from "next/link";
import { useEffect, useState } from "react";
import { UserAuth } from "@/app/context/AuthContext";
import ThemeSwitch from "@/app/components/ThemeSwitch";
import TestingCornerNavItem from "./TestingCornerNavItem";
import { useParams } from "next/navigation";

export default function ManageNavbar() {
  const { googleSignIn, logOut, vxUserInfo } = UserAuth();

  const [isClient, setIsClient] = useState(false);
  let { eventId } = useParams();

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

  return (
    <div className="h-20 w-full border-b-2 flex items-center justify-between p-2 bg-light dark:bg-gray-800">
      <ul className="flex">
        <li className="p-2 cursor-pointer">
          <Link href="/appManage">Home</Link>
        </li>
        {vxUserInfo && (
          <>
            <li className="p-2 cursor-pointer">
              <Link href="/appManage/profile">Profile</Link>
            </li>

            <li className="p-2 cursor-pointer">
              <Link href="/appManage/events">Events</Link>
            </li>

            {eventId && (
              <>
                <li className="p-2 cursor-pointer">
                  <Link href={`/appManage/events/${eventId}/products`}>
                    Products
                  </Link>
                </li>
                <li className="p-2 cursor-pointer">
                  <Link href={`/appManage/events/${eventId}/sellingPoints`}>
                    SellingPoints
                  </Link>
                </li>
              </>
            )}
          </>
        )}

        <TestingCornerNavItem />
      </ul>
      <ul className="flex">
        {isClient &&
          (vxUserInfo === null ? (
            <li onClick={handleSignIn} className="p-2 cursor-pointer">
              Login
            </li>
          ) : (
            <>
              <li className="p-2 cursor-pointer">{vxUserInfo?.email}</li>
              <li onClick={handleSignOut} className="p-2 cursor-pointer">
                Logout
              </li>
            </>
          ))}
        <ThemeSwitch />
      </ul>
    </div>
  );
}
