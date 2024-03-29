import { useEffect, useState } from "react";
import { UserAuth } from "@/app/context/AuthContext";
import { paymentAPI } from "@/api/payment";

const DepositFiatModal = ({ currency }) => {
  const { vxUserInfo } = UserAuth();

  const [depositFiatParams, setDepositFiatParams] = useState({
    userId: vxUserInfo?.id,
    currency: currency,
    amount: "500",
  });
  const [modalOpen, setModalOpen] = useState(false);

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    setDepositFiatParams((prevData) => ({
      ...prevData,
      [id]: value,
    }));
  };

  const handleDepositFiat = (e) => {
    e.preventDefault();
    paymentAPI
      .depositFiat(vxUserInfo?.vxToken, depositFiatParams)
      .then((response) => {
        console.log("handleDepositFiat response:", response.data);
        setModalOpen(false);
        window.open(response.data.payUrl, "_blank");
      })
      .catch((error) => {
        console.error("Error handleDepositFiat:", error);
        // Handle error
      });
  };

  return (
    <div>
      <button
        type="button"
        onClick={() => setModalOpen(true)}
        className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
      >
        Deposit {currency}
      </button>

      {modalOpen && (
        <div className="fixed inset-0 flex items-center justify-center bg-gray-500 bg-opacity-75">
          <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-md dark:bg-gray-800">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-semibold">
                Deposit {currency} {depositFiatParams.amount}
              </h2>
              <button
                onClick={() => setModalOpen(false)}
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
            <form onSubmit={handleDepositFiat}>
              <input
                type="number"
                placeholder="Enter amount"
                className="border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:border-blue-500 mb-4 w-full"
                id="amount"
                value={depositFiatParams.amount}
                onChange={handleInputChange}
              />
              <div className="flex justify-end">
                <button
                  type="button"
                  onClick={() => setModalOpen(false)}
                  className="py-2.5 px-5 me-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="py-2.5 px-5 ms-3 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-4 focus:ring-blue-500"
                >
                  Deposit
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default DepositFiatModal;
