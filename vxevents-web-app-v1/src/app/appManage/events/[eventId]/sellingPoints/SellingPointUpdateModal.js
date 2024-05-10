"use client";
import { useState, useRef, useEffect } from "react";
import { UserAuth } from "@/app/context/AuthContext";
import { publicEventSellingPointAPI } from "@/api/publicEventSellingPoint";
import { useVxContext } from "@/app/context/VxContext";
import { useParams } from "next/navigation";

export default function SellingPointUpdateModal({
  pSellingPointId,
  pTitle,
  pSelectedProducts,
  pAllProducts,
}) {
  const { vxUserInfo } = UserAuth();
  const [modalOpen, setModalOpen] = useState(false);
  const { fetchSellingPoints } = useVxContext();
  const { eventId } = useParams();

  const [title, setTitle] = useState(pTitle);
  const [updatedTitle, setUpdatedTitle] = useState(pTitle);

  const [selectedProducts, setSelectedProducts] = useState(pSelectedProducts);
  const [allProducts, setAllProducts] = useState(pAllProducts);

  const [missingProducts, setMissingProducts] = useState([]);

  const modalRef = useRef(null);

  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [modalOpen]);

  const handleClickOutside = (event) => {
    if (modalRef.current && !modalRef.current.contains(event.target)) {
      closeModal();
    }
  };

  const handleUpdateTitle = (e) => {
    setUpdatedTitle(e.target.value); // Updates local state, does not affect parent component
  };

  const openModal = () => setModalOpen(true);
  const closeModal = () => setModalOpen(false);

  const formatPrice = (price) => `${(price / 100).toFixed(2)}`;

  const addItemToSelectedProducts = (product) => {
    setSelectedProducts([...selectedProducts, product]);
    setMissingProducts(missingProducts.filter((p) => p.id !== product.id));
  };

  const removeFromSelectedProducts = (product) => {
    setMissingProducts([...missingProducts, product]);
    setSelectedProducts(selectedProducts.filter((p) => p.id !== product.id));
  };

  const getProductIdList = () => selectedProducts.map((product) => product.id);

  const callUpdateSellingPoint = () => {
    publicEventSellingPointAPI
      .update(vxUserInfo.vxToken, pSellingPointId, {
        title: updatedTitle,
        productIdList: getProductIdList(),
      })
      .then((response) => {
        console.log("Selling point updated:", response.data);
        closeModal();
        setTitle(updatedTitle);
      })
      .catch((error) => {
        console.error("Error updating selling point:", error);
      });
  };

  if (modalOpen) {
    console.log("Modal is about to open.");
    console.log("allProducts", allProducts);
  }

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
                <h3 className="text-lg font-semibold mb-4">
                  Update: {updatedTitle}
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
                        Title
                      </label>
                      <input
                        type="text"
                        className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                        placeholder="Placeholder"
                        required
                        defaultValue={title} // Binding local state to the input
                        onChange={handleUpdateTitle} // Using the new handler
                      />
                    </div>
                  </div>
                </form>

                {/* selected products */}
                <div>
                  <div>Selected products</div>
                  <div className="flex flex-wrap gap-2 pt-2 pb-2 border-b dark:border-gray-600">
                    {selectedProducts.map((product) => (
                      <span
                        key={product.id}
                        className="cursor-pointer bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-0.5 rounded-full dark:bg-blue-900 dark:text-blue-300"
                        onClick={() => removeFromSelectedProducts(product)}
                      >
                        {`${product.title} €${formatPrice(product.price)}`}
                      </span>
                    ))}
                  </div>
                </div>

                {/* missing products */}
                <div>
                  <div>Missing products</div>
                  <div className="flex flex-wrap gap-2 pt-2 pb-2 border-b dark:border-gray-600">
                    {missingProducts.map((product) => (
                      <span
                        key={product.id}
                        className="cursor-pointer bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-0.5 rounded-full dark:bg-blue-900 dark:text-blue-300"
                        onClick={() => addItemToSelectedProducts(product)}
                      >
                        {`${product.title} €${formatPrice(product.price)}`}
                      </span>
                    ))}
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
                    onClick={callUpdateSellingPoint}
                  >
                    Update
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
      <button onClick={openModal}>{title}</button>
    </div>
  );
}
