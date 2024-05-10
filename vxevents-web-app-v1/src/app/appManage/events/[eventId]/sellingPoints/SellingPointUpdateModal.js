"use client";
import { useState, useRef, useEffect } from "react";
import { UserAuth } from "@/app/context/AuthContext";
import { publicEventSellingPointAPI } from "@/api/publicEventSellingPoint";
import { useVxContext } from "@/app/context/VxContext";

export default function SellingPointUpdateModal({
  sellingPointId,
  title: initialTitle,
  selectedProducts: initialSelectedProducts,
  allProducts,
}) {
  const { vxUserInfo } = UserAuth();
  const [modalOpen, setModalOpen] = useState(false);
  const { fetchSellingPoints } = useVxContext();
  
  const [updateParams, setUpdateParams] = useState({
    vxUserId: vxUserInfo?.id,
    title: initialTitle || "Selling point 001",
  });
  
  const [selectedProducts, setSelectedProducts] = useState(initialSelectedProducts || []);
  const [missingProducts, setMissingProducts] = useState(
    allProducts.filter(product => !initialSelectedProducts.some(p => p.id === product.id))
  );

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

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    setUpdateParams(prev => ({ ...prev, [id]: value }));
  };

  const openModal = () => setModalOpen(true);
  const closeModal = () => setModalOpen(false);

  const formatPrice = (price) => `${(price / 100).toFixed(2)}`;

  const addItemToSelectedProducts = (product) => {
    setSelectedProducts([...selectedProducts, product]);
    setMissingProducts(missingProducts.filter(p => p.id !== product.id));
  };

  const removeFromSelectedProducts = (product) => {
    setMissingProducts([...missingProducts, product]);
    setSelectedProducts(selectedProducts.filter(p => p.id !== product.id));
  };

  const getProductIdList = () => selectedProducts.map(product => product.id);

  const callUpdateSellingPoint = () => {
    publicEventSellingPointAPI.update(vxUserInfo.vxToken, sellingPointId, {
      title: updateParams.title,
      productIdList: getProductIdList(),
    }).then(response => {
      console.log("Selling point updated:", response.data);
      closeModal();
      fetchSellingPoints(vxUserInfo.vxToken);
    }).catch(error => {
      console.error("Error updating selling point:", error);
    });
  };

  return (
    <div>
      {modalOpen && (
        <div className="fixed inset-0 z-10 overflow-y-auto">
          <div className="flex items-center justify-center min-h-screen">
            <div className="fixed inset-0 transition-opacity" aria-hidden="true">
              <div className={`absolute inset-0 bg-gray-500 opacity-50`}></div>
            </div>
            <div ref={modalRef} className="relative bg-white dark:bg-gray-900 rounded-lg p-8 max-w-md">
              <h3 className="text-lg font-semibold mb-4">Update Selling Point</h3>
              <div>
                <input
                  type="text"
                  id="title"
                  className="input-text"
                  value={updateParams.title}
                  onChange={handleInputChange}
                  placeholder="Update title"
                />
              </div>
              <div>
                <button onClick={closeModal}>Cancel</button>
                <button onClick={callUpdateSellingPoint}>Update</button>
              </div>
            </div>
          </div>
        </div>
      )}
      <button onClick={openModal}>{updateParams.title}</button>
    </div>
  );
}
