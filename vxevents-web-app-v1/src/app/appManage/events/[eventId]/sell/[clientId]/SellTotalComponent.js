"use client";
import { useSellContext } from "@/app/context/SellContext";
import { MdOutlineSettings } from "react-icons/md";
import { BiReset } from "react-icons/bi";
import { FaCheckCircle, FaCheckDouble } from "react-icons/fa";

export default function SellTotalComponent() {
  const { sellItemList, showToolBar, setShowToolBar } = useSellContext();

  const switchToolBar = () => {
    setShowToolBar(!showToolBar);
  };

  // Function to compute the total price of all items in the sellItemList array
  function computeTotalPrice(items) {
    return items.reduce((total, item) => {
      return total + item.price;
    }, 0);
  }

  const formatPrice = (price) => `${(price / 100).toFixed(2)}`;

  const totalPrice = computeTotalPrice(sellItemList);

  return (
    <div className="flex gap-4 items-center block max-w-xl p-6 bg-white border border-gray-200 rounded-lg shadow hover:bg-gray-100 dark:bg-gray-800 dark:border-gray-700 dark:hover:bg-gray-700">
      <MdOutlineSettings onClick={switchToolBar} />

      <BiReset />

      <h5 className="ml-4 text-2xl font-bold tracking-tight text-gray-900 dark:text-white mr-4">
        {formatPrice(totalPrice)} Euro
      </h5>

      <FaCheckDouble />
    </div>
  );
}
