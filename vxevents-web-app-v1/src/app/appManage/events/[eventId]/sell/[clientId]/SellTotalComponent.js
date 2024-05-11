"use client";
import { useSellContext } from "@/app/context/SellContext";
import { MdOutlineSettings } from "react-icons/md";
import { BiReset } from "react-icons/bi";
import { FaCheckDouble } from "react-icons/fa";

export default function SellTotalComponent() {
  const {
    sellItemList,
    showToolBar,
    setShowToolBar,
    resetSelectedItems,
    setAddItems,
    displayToast,
    showToast,
  } = useSellContext();


  const switchToolBar = () => {
    setShowToolBar(!showToolBar);
  };

  const handleReset = () => {
    resetSelectedItems();
    setAddItems(true);
  };

  const handleSubmit = () => {
    resetSelectedItems();
    setAddItems(true);
    displayToast();
  };

  // Function to compute the total price of all items in the sellItemList array
  function computeTotalPrice(items) {
    return items.reduce((total, item) => {
      return total + item.price;
    }, 0);
  }

  const formatPrice = (price) => `${(price / 100).toFixed(2)}`;

  const totalPrice = computeTotalPrice(sellItemList);

  let okButton = (
    <FaCheckDouble
      className="w-10 h-10 stroke-current text-gray-500" // Disabled state
    />
  );

  /**
   * If sellItemList is not empty, then add the okButton to the component with click functionality
   */
  if (sellItemList.length > 0) {
    okButton = (
      <FaCheckDouble
        className="w-10 h-10 stroke-current text-green-500 cursor-pointer" // Enabled state with a suitable color for 'ok'
        onClick={handleSubmit}
      />
    );
  }

  return (
    <div className="flex gap-4 items-center block max-w-xl p-6 bg-white border border-gray-200 rounded-lg shadow hover:bg-gray-100 dark:bg-gray-800 dark:border-gray-700 dark:hover:bg-gray-700">
      <BiReset onClick={handleReset} />{" "}
      <MdOutlineSettings onClick={switchToolBar} />
      <h5 className="ml-6 text-2xl font-bold tracking-tight text-gray-900 dark:text-white mr-4">
        {formatPrice(totalPrice)} Euro
      </h5>
      {okButton}
    </div>
  );
}
