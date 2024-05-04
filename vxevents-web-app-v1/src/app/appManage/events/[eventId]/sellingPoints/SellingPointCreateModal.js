"use client";
import { useState, useRef, useEffect } from "react";
import QRCode from "qrcode.react";
import { useParams } from "next/navigation";

export default function SellingPointCreateModal() {
  const [modalOpen, setModalOpen] = useState(false);
  const { eventId } = useParams();

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
            <span className="text-gray-500 dark:text-gray-400"
            onClick={openModal}>
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
                <h3 className="text-lg font-semibold mb-4">Modal Title</h3>
                <p className="text-gray-700 dark:text-gray-400">
                  VxEvents. Please scan this to join the fun
                </p>
              </div>
              <div className="mt-6 text-center">
                <div className="flex flex-col items-center pb-4">
                  <QRCode value={qrMessage} />
                </div>
                <button
                  className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
                  onClick={closeModal}
                >
                  Close
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
