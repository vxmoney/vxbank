"use client";
import { pingAPI } from "@/api/ping";
import { useEffect, useState } from "react";

export default function UsageExamples() {
  const [activeTab, setActiveTab] = useState("Ping");

  return (
    <div class="text-sm font-medium text-center text-gray-500 border-b border-gray-200 dark:text-gray-400 dark:border-gray-700">
      <ul class="flex flex-wrap -mb-px">
        <li class="me-2">
          <a
            href="#"
            class="inline-block p-4 border-b-2 border-transparent rounded-t-lg hover:text-gray-600 hover:border-gray-300 dark:hover:text-gray-300"
            onClick={() => setActiveTab("Ping")}
          >
            Ping
          </a>
        </li>
        <li class="me-2">
          <a
            href="#"
            class="inline-block p-4 text-blue-600 border-b-2 border-blue-600 rounded-t-lg active dark:text-blue-500 dark:border-blue-500"
            aria-current="page"
            onClick={() => setActiveTab("Hello")}
          >
            Hello
          </a>
        </li>
      </ul>
      {activeTab === "Ping" && <div>Ping content</div>}
      {activeTab === "Hello" && <div>Hello content</div>}
    </div>
  );
}
