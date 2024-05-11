"use client";
import { useSellContext } from "@/app/context/SellContext";

export default function SellTotalComponent() {
  const { sellItemList } = useSellContext();

  // Function to compute the total price of all items in the sellItemList array
function computeTotalPrice(items) {
  return items.reduce((total, item) => {
      return total + item.price;
  }, 0);
}

const formatPrice = (price) => `${(price / 100).toFixed(2)}`;

const totalPrice = computeTotalPrice(sellItemList);

  return (
    <div
      href="#"
      className="block max-w-xl p-6 bg-white border border-gray-200 rounded-lg shadow hover:bg-gray-100 dark:bg-gray-800 dark:border-gray-700 dark:hover:bg-gray-700"
    >
      <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
        {formatPrice(totalPrice)} Euro
      </h5>
    </div>
  );
}
