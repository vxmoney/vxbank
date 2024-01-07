"use client";
import { pingAPI } from "@/api/ping";
import { useEffect, useState } from "react";
import GetEnvironmentExample from "./getEnvironmentExample";
import LocalAuthExample from "./localAuth";
import AppEngineAuthExample from "./appEngineAuth";
import IdPathParams from "./idPathParams";

export default function UsageExamples() {
  const [activeTab, setActiveTab] = useState("Ping");

  return (
    <div>
      <div className="text-sm font-medium text-center text-gray-500 border-b border-gray-200 dark:text-gray-400 dark:border-gray-700">
        <ul className="flex flex-wrap -mb-px">
          <li className="me-2">
            <a
              href="#"
              className={`inline-block p-4 rounded-t-lg hover:text-gray-600 hover:border-gray-300 dark:hover:text-gray-300 ${
                activeTab === "Ping"
                  ? "border-b-2 text-blue-600 border-blue-600 active dark:text-blue-500 dark:border-blue-500"
                  : ""
              }`}
              onClick={() => setActiveTab("Ping")}
            >
              Ping
            </a>
          </li>
          <li className="me-2">
            <a
              href="#"
              className={`inline-block p-4 rounded-t-lg hover:text-gray-600 hover:border-gray-300 dark:hover:text-gray-300 ${
                activeTab === "Localhost"
                  ? "border-b-2 text-blue-600 border-blue-600 active dark:text-blue-500 dark:border-blue-500"
                  : ""
              }`}
              onClick={() => setActiveTab("Localhost")}
            >
              Localhost auth
            </a>
          </li>

          <li className="me-2">
            <a
              href="#"
              className={`inline-block p-4 rounded-t-lg hover:text-gray-600 hover:border-gray-300 dark:hover:text-gray-300 ${
                activeTab === "AppEngine"
                  ? "border-b-2 text-blue-600 border-blue-600 active dark:text-blue-500 dark:border-blue-500"
                  : ""
              }`}
              onClick={() => setActiveTab("AppEngine")}
            >
              AppEngine auth
            </a>
          </li>

          <li className="me-2">
            <a
              href="#"
              className={`inline-block p-4 rounded-t-lg hover:text-gray-600 hover:border-gray-300 dark:hover:text-gray-300 ${
                activeTab === "PathParams"
                  ? "border-b-2 text-blue-600 border-blue-600 active dark:text-blue-500 dark:border-blue-500"
                  : ""
              }`}
              onClick={() => setActiveTab("PathParams")}
            >
              Path params
            </a>
          </li>

        </ul>
      </div>

      {activeTab === "Ping" && <GetEnvironmentExample />}
      {activeTab === "Localhost" && <LocalAuthExample />}
      {activeTab === "AppEngine" && <AppEngineAuthExample />}
      {activeTab === "PathParams" && <IdPathParams />}
    </div>
  );
}
