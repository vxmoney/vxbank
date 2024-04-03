import { useState, useRef } from "react";
import QRCode from "qrcode.react";

export default function OnboardClientsComponent() {
  const [modalOpen, setModalOpen] = useState(false);
  const [qrMessage, setQrMessage] = useState("hello qr code message");
  const modalRef = useRef(null);

  const openModal = () => {
    setModalOpen(true);
  };

  const closeModal = () => {
    setModalOpen(false);
  };

  return (
    <div>
      <div className="pl-8 pr-8 pt-8">
        <div className="block  p-6 bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700 ">
          <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
            OnboardClientsComponent
          </h5>
          <p className="font-normal text-gray-700 dark:text-gray-400">
            Use this to onboard new clients
          </p>
          <button
            className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
            onClick={openModal}
          >
            Open Modal
          </button>
        </div>
      </div>
      {modalOpen && (
        <div className="fixed inset-0 z-10 overflow-y-auto">
          <div className="flex items-center justify-center min-h-screen">
            <div className="fixed inset-0 transition-opacity" aria-hidden="true">
              <div className={`absolute inset-0 ${modalOpen ? 'bg-gray-500 opacity-50' : ''} ${modalOpen ? 'dark:bg-gray-800 dark:opacity-50' : ''}`}></div>
            </div>
            <div ref={modalRef} className="relative bg-white dark:bg-gray-900 rounded-lg p-8 max-w-md">
              <div className="text-center">
                <h3 className="text-lg font-semibold mb-4">Modal Title</h3>
                <p className="text-gray-700 dark:text-gray-400">
                  VxEvents. Please scan this to join the fun
                </p>
              </div>
              <div className="mt-6 text-center">
                <QRCode value={qrMessage} />
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
