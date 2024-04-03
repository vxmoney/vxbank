import  { useState } from "react";

export default function OnboardClientsComponent() {
  const [modalOpen, setModalOpen] = useState(false);

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
            className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
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
              <div className="absolute inset-0 bg-gray-500 opacity-75"></div>
            </div>
            <div className="relative bg-white rounded-lg p-8 max-w-md">
              <div className="text-center">
                <h3 className="text-lg font-semibold mb-4">Modal Title</h3>
                <p className="text-gray-700 dark:text-gray-400">
                  VxEvents. Please scan this to join the fun
                </p>
              </div>
              <div className="mt-6 text-center">
                <button
                  className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
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
