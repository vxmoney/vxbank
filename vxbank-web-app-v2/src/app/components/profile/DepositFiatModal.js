"use client";
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

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    console.log("handleInputChange",id,value)
    setDepositFiatParams((prevData) => ({
      ...prevData,
      [id]: value,
    }));
  };

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

  let modalTitle = "deposit-modal-" + currency;

  const handleDepositFiat = (e) => {
    e.preventDefault();
    console.log(depositFiatParams); 
    paymentAPI
      .depositFiat(vxUserInfo?.vxToken, depositFiatParams)
      .then((response) => {
        console.log("handleDepositFiat response:", response.data);
        hideModal(modalTitle);
        // Handle successful response
      })
      .catch((error) => {
        console.error("Error handleDepositFiat:", error);
        //setMessageAndShowAlertForABit("Error handleDepositFiat");
        // Handle error
      });
  };



  

  return (
    <div>
      <button
        type="button"
        data-modal-target={modalTitle}
        data-modal-toggle={modalTitle}
        className="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
      >
        Deposit {currency}
      </button>

      <div
        id={modalTitle}
        tabIndex="-1"
        className="hidden overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full"
      >
        <div className="relative p-4 w-full max-w-md max-h-full">
          <div className="relative bg-white rounded-lg shadow dark:bg-gray-700">
            <button
              type="button"
              className="absolute top-3 end-2.5 text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white"
              data-modal-hide={modalTitle}
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
                Deposit {currency} {depositFiatParams.amount}
              </h3>
              <p className="text-gray-600 mb-8 dark:text-gray-300">
                Please enter the amount you want to deposit:
              </p>
              <input
                type="number"
                placeholder="Enter amount"
                className="border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:border-blue-500"
                id="amount"
                value={depositFiatParams.amount}
                onChange={handleInputChange}
              />
              <div className="mt-6 flex justify-center">
                <button
                  data-modal-hide={modalTitle}
                  type="button"
                  className="py-2.5 px-5 me-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
                >
                  Cancel
                </button>
                <button
                  type="button"
                  className="py-2.5 px-5 ms-3 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-4 focus:ring-blue-500"
                  onClick={handleDepositFiat}
                >
                  Deposit
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DepositFiatModal;
