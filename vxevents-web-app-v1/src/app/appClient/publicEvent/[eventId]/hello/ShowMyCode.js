import { useState, useRef, useEffect } from "react";
import QRCode from "qrcode.react";
import { useParams } from "next/navigation";

export default function ShowMyCodeComponent() {
  const [modalOpen, setModalOpen] = useState(false);
  const { eventId } = useParams();

  const [currentURL, setCurrentURL] = useState("");
  useEffect(() => {
    setCurrentURL(window.location.origin);
  }, []);
  const qrMessage = `${currentURL}/appManage/events/${eventId}/sell/fakeClientId`;

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
      <div className="pl-8 pr-8 pt-8">
        <div className="block  p-6 bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700 ">
          <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
            Show my details component
          </h5>
          <p className="font-normal text-gray-700 dark:text-gray-400">
            Use this to buy stuff
          </p>
          <button
            className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
            onClick={openModal}
          >
            Show My Code
          </button>
        </div>
      </div>
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
                <h3 className="text-lg font-semibold mb-4">My details</h3>
                <p className="text-gray-700 dark:text-gray-400">
                  I love VxEvents. I want to buy more stuff 
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
