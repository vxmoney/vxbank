"use client";

import { UserAuth } from "../context/AuthContext";

export default function Profiler() {
  const { user } = UserAuth();

  if (!user) {
    return (
      <div className="p-4">
        <div class="block max-w-md p-6 bg-white border border-gray-200 rounded-lg shadow hover:bg-gray-100 dark:bg-gray-800 dark:border-gray-700 dark:hover:bg-gray-700">
          <h5 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
            Please login to view your profile
          </h5>
        </div>
      </div>
    );
  }

  return (
    <div className="p-4">
      <div class="block max-w-md p-6 bg-white border border-gray-200 rounded-lg shadow hover:bg-gray-100 dark:bg-gray-800 dark:border-gray-700 dark:hover:bg-gray-700">
        <h5 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
          VxGaming profile
        </h5>
        <div>
          <p class="p-4 font-normal text-gray-700 dark:text-gray-400">
            email: {user.email}
          </p>

          <p class="p-4 font-normal text-gray-700 dark:text-gray-400">
            name: {user.displayName}
          </p>
          <p class="p-4 font-normal text-gray-700 dark:text-gray-400"></p>
          <p class="p-4 font-normal text-gray-700 dark:text-gray-400">
            🏆 Compete, Win, EARN! 🏆
          </p>
        </div>
      </div>
    </div>
  );
}
