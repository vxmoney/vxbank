"use client";
import { useParams } from "next/navigation";
import { useState, useEffect } from "react";
import { UserAuth } from "@/app/context/AuthContext";
import { publicEventAPI } from "@/api/publicEvent";

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
          <p class="font-normal text-gray-700 dark:text-gray-400">
            To be implemented but you can also use postman ...
          </p>
          <p class="font-normal text-gray-700 dark:text-gray-400">
            Your choice ...
          </p>
        </div>
      </div>
    </div>
  );
}
