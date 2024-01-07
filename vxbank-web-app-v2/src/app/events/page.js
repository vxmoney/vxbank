"use client";
import { useState } from "react";
import CreateEventModal from "../components/CreateEventModal";
import EventsComponent from "../components/EventsComponent";

export default function EventsHome() {
  return (
    <div>
      <section className="bg-white dark:bg-gray-900">
        <div className="py-2 px-4 mx-auto max-w-screen-xl lg:py-2 flex justify-between items-center">
          <p className="mb-2 text-lg font-normal text-gray-500 lg:text-xl sm:px-16 lg:px-8 dark:text-gray-400">
            -- Events --
          </p>
          <button
            data-modal-target="default-modal"
            data-modal-toggle="default-modal"
            className="flex items-center"
            type="button"
          >
            <span className="text-gray-500 dark:text-gray-400">Create event</span>
          </button>
        </div>
      </section>

      <CreateEventModal />
      <EventsComponent />
    </div>
  );
}
