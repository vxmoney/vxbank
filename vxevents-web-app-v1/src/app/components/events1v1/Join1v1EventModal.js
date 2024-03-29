"use client";

import { useParams } from "next/navigation";
import { useState, useEffect } from "react";
import { eventAPI } from "@/api/event";
import { UserAuth } from "../../context/AuthContext";

const Join1v1EventModal = ({ fetchParticipants }) => {
  const { vxUserInfo } = UserAuth();
  let { eventId } = useParams();
  const [eventData, setEventData] = useState(null);
  const [showAlert, setShowAlert] = useState(false);
  const [alertMessage, setAlertMessage] = useState("Nothing to show here");

  function setMessageAndShowAlertForABit(message) {
    setAlertMessage(message);
    setShowAlert(true);
    setTimeout(() => {
      setShowAlert(false);
    }, 3000);
  }

  const eventJoinParams = {
    vxUserId: vxUserInfo?.id,
    eventId: eventId,
  };

  // load event
  useEffect(() => {
    const fetchEvent = async () => {
      try {
        const response = await eventAPI.getById(vxUserInfo?.vxToken, eventId);
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
  const showModal = (modalId) => {
    const modal = document.getElementById(modalId);
    modal.classList.remove("hidden");
    modal.classList.add("flex");
  };

  // Function to hide the modal
  const hideModal = (modalId) => {
    const modal = document.getElementById(modalId);
    modal.classList.add("hidden");
    modal.classList.remove("flex");
  };

  // Event listener for showing the modal
  document.querySelectorAll("[data-modal-toggle]").forEach((button) => {
    button.addEventListener("click", () =>
      showModal(button.dataset.modalTarget)
    );
  });

  // Event listener for hiding the modal
  document.querySelectorAll("[data-modal-hide]").forEach((button) => {
    button.addEventListener("click", () => hideModal(button.dataset.modalHide));
  });
  //</show hide modal section>

  const handleJoinSubmit = (e) => {
    e.preventDefault();
    // Here, you can use formData to send the object to your endpoint or perform any other actions
    console.log(eventJoinParams); // Assuming eventJoinParams contains the necessary parameters for joining an event
    eventAPI
      .join(vxUserInfo?.vxToken, eventJoinParams)
      .then((response) => {
        console.log("Joined event response:", response.data);
        fetchParticipants();
        hideModal("join-modal");
        // Handle successful response
      })
      .catch((error) => {
        console.error("Error joining event:", error);
        setMessageAndShowAlertForABit("Error joining event");
        // Handle error
      });
  };

  const handleCloseSubmit = (e) => {
    e.preventDefault();
    let eventCloseParams = {
      vxEventId: eventId,
    };
    // Here, you can use formData to send the object to your endpoint or perform any other actions
    console.log(eventJoinParams); // Assuming eventJoinParams contains the necessary parameters for joining an event
    eventAPI
      .closeEvent(vxUserInfo?.vxToken, eventCloseParams)
      .then((response) => {
        console.log("Close event response:", response.data);
        fetchParticipants();
        hideModal("join-modal");
        // Handle successful response
      })
      .catch((error) => {
        console.error("Error close event:", error);
        setMessageAndShowAlertForABit("Error closing event");
        // Handle error
      });
  };


  const handlePayJoinSubmit = (e) => {
    e.preventDefault();
    // Here, you can use formData to send the object to your endpoint or perform any other actions
    console.log(eventJoinParams); // Assuming eventJoinParams contains the necessary parameters for joining an event
    eventAPI
      .payJoin(vxUserInfo?.vxToken, eventJoinParams)
      .then((response) => {
        console.log("Joined event response:", response.data);
        hideModal("join-modal");
        window.open(response.data.stripeSessionPaymentUrl, "_blank")
      })
      .catch((error) => {
        console.error("Error joining event:", error);
        setMessageAndShowAlertForABit("Error joining event");
        // Handle error
      });
  };

  

  const myToast = showAlert && (
    <div
      id="toast-simple"
      class="flex items-center w-full max-w-xs p-4 space-x-4 rtl:space-x-reverse text-gray-500 bg-white divide-x rtl:divide-x-reverse divide-gray-200 rounded-lg shadow dark:text-gray-400 dark:divide-gray-700 space-x dark:bg-gray-800 fixed bottom-5 right-5"
      role="alert"
    >
      <svg
        class="w-5 h-5 text-blue-600 dark:text-blue-500 rotate-45"
        aria-hidden="true"
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 18 20"
      >
        <path
          stroke="currentColor"
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="m9 17 8 2L9 1 1 19l8-2Zm0 0V9"
        />
      </svg>
      <div class="ps-4 text-sm font-normal">{alertMessage}</div>
      <button
        type="button"
        class="ms-auto -mx-1.5 -my-1.5 bg-white text-gray-400 hover:text-gray-900 rounded-lg focus:ring-2 focus:ring-gray-300 p-1.5 hover:bg-gray-100 inline-flex items-center justify-center h-8 w-8 dark:text-gray-500 dark:hover:text-white dark:bg-gray-800 dark:hover:bg-gray-700"
        data-dismiss-target="#toast-warning"
        aria-label="Close"
        onClick={() => setShowAlert(false)}
      >
        <span class="sr-only">Close</span>
        <svg
          class="w-3 h-3"
          aria-hidden="true"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 14 14"
        >
          <path
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"
          />
        </svg>
      </button>
    </div>
  );

  return (
    <div className="pt-8 pl-8">
      <button
        type="button"
        data-modal-target="join-modal"
        data-modal-toggle="join-modal"
        className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
      >
        Join event
      </button>

      <div
        id="join-modal"
        tabIndex="-1"
        className="hidden overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full"
      >
        <div className="relative p-4 w-full max-w-md max-h-full">
          <div className="relative bg-white rounded-lg shadow dark:bg-gray-700">
            <button
              type="button"
              className="absolute top-3 end-2.5 text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white"
              data-modal-hide="join-modal"
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
                type="button"
                data-modal-hide="join-modal"
                className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
                onClick={handleJoinSubmit}
              >
                Yes, I&apos;m sure
              </button>
              <button
                data-modal-hide="join-modal"
                type="button"
                className="py-2.5 px-5 ms-3 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
              >
                No, cancel
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Pay Join Modal */}

      <button
        type="button"
        data-modal-target="pay-join-modal"
        data-modal-toggle="pay-join-modal"
        className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
      >
        Pay join event
      </button>

      <div
        id="pay-join-modal"
        tabIndex="-1"
        className="hidden overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full"
      >
        <div className="relative p-4 w-full max-w-md max-h-full">
          <div className="relative bg-white rounded-lg shadow dark:bg-gray-700">
            <button
              type="button"
              className="absolute top-3 end-2.5 text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white"
              data-modal-hide="pay-join-modal"
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
                Are you sure you want to pay and join this event?
              </h3>
              <button
                type="button"
                data-modal-hide="pay-join-modal"
                className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
                onClick={handlePayJoinSubmit}
              >
                Yes, I&apos;m sure
              </button>
              <button
                data-modal-hide="pay-join-modal"
                type="button"
                className="py-2.5 px-5 ms-3 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
              >
                No, cancel
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Close Event Modal */}
      <button
        type="button"
        data-modal-target="close-event-modal"
        data-modal-toggle="close-event-modal"
        className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
      >
        Close event
      </button>

      <div
        id="close-event-modal"
        tabIndex="-1"
        className="hidden overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full"
      >
        <div className="relative p-4 w-full max-w-md max-h-full">
          <div className="relative bg-white rounded-lg shadow dark:bg-gray-700">
            <button
              type="button"
              className="absolute top-3 end-2.5 text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white"
              data-modal-hide="close-event-modal"
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
                Are you sure you want to close this event?
              </h3>
              <button
                data-modal-hide="close-event-modal"
                type="button"
                className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
                onClick={handleCloseSubmit}
              >
                Yes, I&apos;m sure
              </button>
              <button
                data-modal-hide="close-event-modal"
                type="button"
                className="py-2.5 px-5 ms-3 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
              >
                No, cancel
              </button>
            </div>
          </div>
        </div>
      </div>

      {myToast}
    </div>
  );
};

export default Join1v1EventModal;
