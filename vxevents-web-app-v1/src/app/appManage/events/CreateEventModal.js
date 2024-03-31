"use client";
import { useEffect, useState } from "react";
import { UserAuth } from "../../context/AuthContext";
import { publicEventAPI } from "@/api/publicEvent";
import { useVxContext } from "../../context/VxContext";

const CreateEventModal = () => {
  const { vxUserInfo } = UserAuth();
  const { fetchEvents } = useVxContext();

  const [eventCreateParams, setEventCreateParams] = useState({
    vxUserId: vxUserInfo?.id,
    type: "payed1V1",
    vxIntegrationId: "vxEvents",
    title: "Example 001",
    currency: "eur",
  });

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    setEventCreateParams((prevData) => ({
      ...prevData,
      [id]: value,
    }));
  };

  const closeModal = () => {
    const modal = document.getElementById("default-modal");
    modal.classList.add("hidden");
    modal.setAttribute("aria-hidden", "true");
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Here, you can use formData to send the object to your endpoint or perform any other actions
    console.log(eventCreateParams);
    publicEventAPI
      .create(vxUserInfo?.vxToken, eventCreateParams)
      .then((response) => {
        console.log("PublicEvent created:", response.data);
        fetchEvents(vxUserInfo?.vxToken)
        closeModal();
        // Handle successful response
      })
      .catch((error) => {
        console.error("Error creating event:", error);
        // Handle error
      });
  };

  useEffect(() => {
    const modalButton = document.querySelector(
      '[data-modal-toggle="default-modal"]'
    );
    const modal = document.getElementById("default-modal");
    const closeModalButtons = document.querySelectorAll(
      '[data-modal-hide="default-modal"]'
    );

    const toggleModal = () => {
      modal.classList.toggle("hidden");
      modal.setAttribute("aria-hidden", String(!modal.hidden));
    };

    const closeModal = () => {
      modal.classList.add("hidden");
      modal.setAttribute("aria-hidden", "true");
    };

    if (modalButton) {
      modalButton.addEventListener("click", toggleModal);
    }

    closeModalButtons.forEach((button) => {
      button.addEventListener("click", closeModal);
    });

    return () => {
      if (modalButton) {
        modalButton.removeEventListener("click", toggleModal);
      }

      closeModalButtons.forEach((button) => {
        button.removeEventListener("click", closeModal);
      });
    };
  }, []);

  return (
    <div
      id="default-modal"
      tabIndex="-1"
      aria-hidden="true"
      className="hidden overflow-y-auto overflow-x-hidden fixed inset-x-0 top-1/8 z-50 flex items-center justify-center w-full"
    >
      <div className="relative p-4 w-full">
        {/* Modal content */}
        <div className="relative bg-white rounded-lg shadow dark:bg-gray-700">
          {/* Modal header */}
          <div className="flex items-center justify-between p-4 md:p-5 border-b rounded-t dark:border-gray-600">
            <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
              Create event
            </h3>
            <button
              type="button"
              className="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white"
              data-modal-hide="default-modal"
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
          </div>
          {/* Modal body */}

          <form
            className="p-4 md:p-5 border-b rounded-t dark:border-gray-600"
            onSubmit={handleSubmit}
          >
            <div className="grid gap-6 mb-6 md:grid-cols-2">
              {/* Title Section */}
              <div>
                <label
                  htmlFor="title"
                  className="block mb-2 text-sm font-medium text-gray-900 dark:text-white"
                >
                  Event title
                </label>
                <input
                  type="text"
                  className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                  placeholder="League of legends challenge"
                  required
                  id="title"
                  value={eventCreateParams.title}
                  onChange={handleInputChange}
                />
              </div>

              
            </div>
          </form>

          {/* Modal footer */}
          <div className="flex items-center p-4 md:p-5 border-t border-gray-200 rounded-b dark:border-gray-600">
            <button
              type="button"
              className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
              onClick={handleSubmit}
            >
              I accept
            </button>
            <button
              data-modal-hide="default-modal"
              type="button"
              className="ms-3 text-gray-500 bg-white hover:bg-gray-100 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg border border-gray-200 text-sm font-medium px-5 py-2.5 hover:text-gray-900 focus:z-10 dark:bg-gray-700 dark:text-gray-300 dark:border-gray-500 dark:hover:text-white dark:hover:bg-gray-600 dark:focus:ring-gray-600"
            >
              Decline
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreateEventModal;
