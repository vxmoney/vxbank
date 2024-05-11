"use client";
import { useState, useRef, useEffect } from "react";
import { useVxContext } from "@/app/context/VxContext";

export default function SellingPointDefaultModal({ sellingPointId, title }) {

  

  // <modal functionality>
  const [modalOpen, setModalOpen] = useState(false);
  const modalRef = useRef(null);

  const openModal = () => setModalOpen(true);
  const closeModal = () => setModalOpen(false);

  // define function to handle clicks outside the modal
  const handleClickOutside = (event) => {
    if (modalRef.current && !modalRef.current.contains(event.target)) {
      closeModal();
    }
  };

  // add event listener to detect clicks outside the modal
  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [modalOpen]);

  // </modal functionality>

  const { defaultSellingPointId, setDefaultSellingPointId } = useVxContext();
  console.log("defaultSellingPointId", defaultSellingPointId);

  return (
    <div>
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
                <h3 className="text-lg font-semibold mb-4">{title}</h3>
              </div>
              <div className="mt-6 text-center">
                {/* selected products */}
                <div>
                  <div>
                    Set {title} as your personal default selling point?{" "}
                  </div>
                </div>

                <div className="flex pt-4 justify-center">
                  <button
                    className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
                    onClick={closeModal}
                  >
                    Cancel
                  </button>
                  <button
                    className="py-2.5 px-5 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-blue-500 hover:text-white focus:z-10 focus:ring-4 focus:ring-blue-500 dark:focus:ring-blue-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-blue-700"
                    // onClick={callUpdateSellingPoint}
                  >
                    Set As Default
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      <div onClick={openModal}> Modal {title} </div>
    </div>
  );
}
