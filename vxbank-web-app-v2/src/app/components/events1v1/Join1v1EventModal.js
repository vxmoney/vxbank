"use client";

import { useParams } from "next/navigation";
import { useState, useEffect } from "react";
import { eventAPI } from "@/api/event";
import { UserAuth } from "../../context/AuthContext";



const Join1v1EventModal = () => {

  const { vxUserInfo } = UserAuth();
  let { eventId } = useParams();
  const [eventData, setEventData] = useState(null);

  // load event
  useEffect(() => {
    const fetchEvent = async () => {
      try {
        const response = await eventAPI.getById(vxUserInfo?.vxToken, eventId);
        console.log("event data from modal: ", response.data);
        setEventData(response.data);
      } catch (error) {
        console.error("Error fetching event:", error);
      }
    };

    if (eventId && vxUserInfo && vxUserInfo?.vxToken) {
      fetchEvent();
    }
  }, [eventId]);

  //<show hide modal section>
  // Function to show the modal
  const showModal = () => {
    const modal = document.getElementById("popup-modal");
    modal.classList.remove("hidden");
    modal.classList.add("flex");
  };

  // Function to hide the modal
  const hideModal = () => {
    const modal = document.getElementById("popup-modal");
    modal.classList.add("hidden");
    modal.classList.remove("flex");
  };

  // Event listener for showing the modal
  document.querySelectorAll("[data-modal-toggle]").forEach((button) => {
    button.addEventListener("click", showModal);
  });

  // Event listener for hiding the modal
  document.querySelectorAll("[data-modal-hide]").forEach((button) => {
    button.addEventListener("click", hideModal);
  });
  //</show hide modal section>

  return (
    <div className="pt-8 pl-8">
      <button
        data-modal-target="popup-modal"
        data-modal-toggle="popup-modal"
        className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
        type="button"
      >
        Join event
      </button>

      <div
        id="popup-modal"
        tabIndex="-1"
        className="hidden overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full"
      >
        <div className="relative p-4 w-full max-w-md max-h-full">
          <div className="relative bg-white rounded-lg shadow dark:bg-gray-700">
            <button
              type="button"
              className="absolute top-3 end-2.5 text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white"
              data-modal-hide="popup-modal"
            >
              <svg
                className="w-3 h-3"
                aria-hidden="true"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 14 14"
              >
                <path
                  stroke="currentColor"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"
                />
              </svg>
              <span className="sr-only">Close modal</span>
            </button>
            <div className="p-4 md:p-5 text-center">
              <h3 className="pt-8 mb-5 text-lg font-normal text-gray-500 dark:text-gray-400">
                Are you sure you want to join this event?
              </h3>
              <button
                data-modal-hide="popup-modal"
                type="button"
                className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
              >
                Yes, I'm sure
              </button>
              <button
                data-modal-hide="popup-modal"
                type="button"
                className="py-2.5 px-5 ms-3 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
              >
                No, cancel
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Join1v1EventModal;
