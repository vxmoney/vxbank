"use client";

import { useSellContext } from "@/app/context/SellContext";

export default function SellProductButtonComponent({ product }) {
  const { sellItemList, processItem } = useSellContext();

  const handleProcessItem = () => {
    processItem(product);
  };

  let productCount = null;
  // iterate over product list and count the number of times the product appears by product.id
  if (sellItemList) {
   let productCountVal = sellItemList.reduce((acc, item) => {
      if (item.id === product.id) {
        return acc + 1;
      }
      return acc;
    }, 0);
    // if the product count is greater than 0, then display the product count
    if (productCountVal > 0) {
      productCount = (
        <div className="absolute inline-flex items-center justify-center w-8 h-8 text-xl font-bold text-white bg-blue-600 border-2 border-white rounded-full -top-2 -right-2 dark:border-gray-900">
          {productCountVal}
        </div>
      );
    }
  }

  return (
    <div className="flex flex-col items-center justify-center">
      <button
        type="button"
        className="relative text-gray-900 bg-white border border-gray-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-full text-sm px-5 py-2.5 me-2 mb-2 dark:bg-gray-800 dark:text-white dark:border-gray-600 dark:hover:bg-gray-700 dark:hover:border-gray-600 dark:focus:ring-gray-700 whitespace-nowrap"
        onClick={handleProcessItem}
      >
        {product.title}

        {productCount}
      </button>
    </div>
  );
}
