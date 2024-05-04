"use client";
import { useState, useRef, useEffect } from "react";
import QRCode from "qrcode.react";
import { useParams } from "next/navigation";
import { UserAuth } from "@/app/context/AuthContext";

export default function SellingPointCreateModal() {
  const { vxUserInfo } = UserAuth();
  const [modalOpen, setModalOpen] = useState(false);
  const { eventId } = useParams();
  const [createParams, setCreateParams] = useState({
    vxUserId: vxUserInfo?.id,
    type: "payed1V1",
    vxIntegrationId: "vxEvents",
    title: "Selling point 001",
    currency: "eur",
  });

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    setCreateParams((prevData) => ({
      ...prevData,
      [id]: value,
    }));
  };

  const [currentURL, setCurrentURL] = useState("");
  useEffect(() => {
    setCurrentURL(window.location.origin);
  }, []);
  const qrMessage = `${currentURL}/appClient/publicEvent/${eventId}`;

  const modalRef = useRef(null);

  const openModal = () => {
    setModalOpen(true);
  };

  const closeModal = () => {
    setModalOpen(false);
  };

  // Effect to handle clicks outside the modal
  useEffect(() => {
    // Function to detect click outside
    const handleClickOutside = (event) => {
      if (modalRef.current && !modalRef.current.contains(event.target)) {
        closeModal();
      }
    };

    // Add event listener when modal is open
    if (modalOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    // Remove event listener on cleanup
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [modalOpen]); // Ensure the effect runs only when modalOpen changes

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
            <span
              className="text-gray-500 dark:text-gray-400"
              onClick={openModal}
            >
              Create sellingPoint
            </span>
          </button>
        </div>
      </section>

      {modalOpen && (
        <div className="fixed inset-0 z-10 overflow-y-auto">
          <div className="flex items-center justify-center min-h-screen">
            <div
              className="fixed inset-0 transition-opacity"
              aria-hidden="true"
            >
              <div
                className={`absolute inset-0 ${
                  modalOpen ? "bg-gray-500 opacity-50" : ""
                } ${modalOpen ? "dark:bg-gray-800 dark:opacity-50" : ""}`}
              ></div>
            </div>
            <div
              ref={modalRef}
              className="relative bg-white dark:bg-gray-900 rounded-lg p-8 max-w-md"
            >
              <div className="text-center">
                <h3 className="text-lg font-semibold mb-4">
                  Create Selling point
                </h3>
              </div>
              <div className="mt-6 text-center">
                <form
                  className="p-4 md:p-5 border-b rounded-t dark:border-gray-600"
                  // onSubmit={handleSubmit}
                >
                  <div className="flex items-center mb-6 flex-no-wrap">
                    {/* Title Section */}
                    <div className="flex items-center mr-6">
                      <label
                        htmlFor="title"
                        className="mb-0 mr-2 text-sm font-medium text-gray-900 dark:text-white shrink-0"
                      >
                        Selling point title
                      </label>
                      <input
                        type="text"
                        className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                        placeholder="League of legends challenge"
                        required
                        id="title"
                        value={createParams.title}
                        onChange={handleInputChange}
                      />
                    </div>
                  </div>
                </form>

                <button
                  className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
                  onClick={closeModal}
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
