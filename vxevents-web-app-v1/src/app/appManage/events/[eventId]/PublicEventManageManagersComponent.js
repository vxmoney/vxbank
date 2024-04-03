"use client";
import { UserAuth } from "@/app/context/AuthContext";
import { useParams } from "next/navigation";

export default function PublicEventManageManagersComponent() {
  const { vxUserInfo } = UserAuth();
  const { eventId } = useParams();

  return (
    <div>
      <div className="pl-8 pr-8 pt-8">
        <div className="block  p-6 bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700 ">
          <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
            Event Managers
          </h5>
          <p className="font-normal text-gray-700 dark:text-gray-400">
            To be implemented but you can also use postman ...
          </p>
          <p className="font-normal text-gray-700 dark:text-gray-400">
            Your choice ...
          </p>
        </div>
      </div>
    </div>
  );
}
