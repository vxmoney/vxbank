"use client";
import { useState } from "react";
import CreateEventModal from "../components/CreateEventModal";

export default function EventsHome() {
  return (
    <div>
      <section class="bg-white dark:bg-gray-900">
        <div class="py-2 px-4 mx-auto max-w-screen-xl lg:py-2 flex justify-between items-center">
          <p class="mb-2 text-lg font-normal text-gray-500 lg:text-xl sm:px-16 lg:px-8 dark:text-gray-400">
            -- Events --
          </p>
          <button
            data-modal-target="default-modal"
            data-modal-toggle="default-modal"
            class="flex items-center"
            type="button"
          >
            <svg
              class="w-6 h-6 text-gray-500 dark:text-gray-400 mr-2"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              fill="currentColor"
              viewBox="0 0 18 20"
            >
              <path d="M16 8H10V2a2 2 0 0 0-4 0v6H2a2 2 0 0 0 0 4h4v6a2 2 0 0 0 4 0v-6h6a2 2 0 0 0 0-4Z" />
            </svg>
            <span class="text-gray-500 dark:text-gray-400">Create event</span>
          </button>
        </div>
      </section>

      
      
      <CreateEventModal />
    </div>
  );
}
