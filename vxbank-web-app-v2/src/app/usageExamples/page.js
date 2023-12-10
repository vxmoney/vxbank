"use client";
import { pingAPI } from "@/api/ping";
import { useEffect, useState } from "react";
import GetEnvironmentExample from "./getEnvironmentExample";

export default function UsageExamples() {
  const [activeTab, setActiveTab] = useState("Ping");

  return (
    <div class="text-sm font-medium text-center text-gray-500 border-b border-gray-200 dark:text-gray-400 dark:border-gray-700">
      <ul class="flex flex-wrap -mb-px">
        <li class="me-2">
          <a
            href="#"
            class={`inline-block p-4 rounded-t-lg hover:text-gray-600 hover:border-gray-300 dark:hover:text-gray-300 ${
              activeTab === "Ping"
                ? "border-b-2 text-blue-600 border-blue-600 active dark:text-blue-500 dark:border-blue-500"
                : ""
            }`}
            onClick={() => setActiveTab("Ping")}
          >
            Ping
          </a>
        </li>
        <li class="me-2">
          <a
            href="#"
            class={`inline-block p-4 rounded-t-lg hover:text-gray-600 hover:border-gray-300 dark:hover:text-gray-300 ${
              activeTab === "Hello"
                ? "border-b-2 text-blue-600 border-blue-600 active dark:text-blue-500 dark:border-blue-500"
                : ""
            }`}
            onClick={() => setActiveTab("Hello")}
          >
            Hello
          </a>
        </li>
      </ul>
      {activeTab === "Ping" && <GetEnvironmentExample/>}
      {activeTab === "Hello" && <div>Hello content</div>}
    </div>
  );
}