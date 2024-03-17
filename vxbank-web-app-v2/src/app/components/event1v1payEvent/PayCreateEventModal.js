import { useEffect, useState } from "react";
import { UserAuth } from "@/app/context/AuthContext";
import { eventAPI } from "@/api/event";

const PayCreateEventModal = () => {
  const { vxUserInfo } = UserAuth();

  const [eventCreateParams, setEventCreateParams] = useState({
    vxUserId: vxUserInfo?.id,
    type: "payed1V1",
    vxIntegrationId: "vxGaming",
    vxGame: "leagueOfLegends",
    title: "Example 001",
    currency: "eur",
    entryPrice: "500",
  });

  const [modalOpen, setModalOpen] = useState(false);

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    setEventCreateParams((prevData) => ({
      ...prevData,
      [id]: value,
    }));
  };

  const closeModal = () => {
    setModalOpen(false);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    eventAPI
      .create(vxUserInfo?.vxToken, eventCreateParams)
      .then((response) => {
        console.log("Event created:", response.data);
        closeModal();
      })
      .catch((error) => {
        console.error("Error creating event:", error);
      });
  };

  return (
    <div>
      <button
        type="button"
        onClick={() => setModalOpen(true)}
        className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
      >
        Create Paid Event
      </button>

      {modalOpen && (
        <div className="fixed inset-0 flex items-center justify-center bg-gray-500 bg-opacity-75">
          <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-md dark:bg-gray-800">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-semibold">
                Create Paid Event: {eventCreateParams.title}
              </h2>
              <button
                onClick={closeModal}
                className="text-gray-400 hover:text-gray-600 focus:outline-none"
              >
                <svg
                  className="w-6 h-6"
                  fill="none"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path d="M6 18L18 6M6 6l12 12"></path>
                </svg>
              </button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="grid gap-6">
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
                    placeholder="League of Legends challenge"
                    required
                    id="title"
                    value={eventCreateParams.title}
                    onChange={handleInputChange}
                  />
                </div>

                {/* Entry Price Section */}
                <div>
                  <label
                    htmlFor="entryPrice"
                    className="block mb-2 text-sm font-medium text-gray-900 dark:text-white"
                  >
                    Entry Price (in cents)
                  </label>
                  <input
                    type="number"
                    className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                    placeholder="Enter price in cents"
                    required
                    id="entryPrice"
                    value={eventCreateParams.entryPrice}
                    onChange={handleInputChange}
                  />
                </div>
              </div>
              <div className="flex justify-end mt-4">
                <button
                  type="button"
                  onClick={closeModal}
                  className="py-2.5 px-5 me-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="py-2.5 px-5 ms-3 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-4 focus:ring-blue-500"
                >
                  Create Event
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default PayCreateEventModal;