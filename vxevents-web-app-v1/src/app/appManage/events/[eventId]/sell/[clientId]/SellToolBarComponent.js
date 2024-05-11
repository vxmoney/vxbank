"use client";
import { useSellContext } from "@/app/context/SellContext";

export default function SellToolBarComponent() {
  const { sellItemList } = useSellContext();

  // Function to compute the total price of all items in the sellItemList array

  return (
    <div
      href="#"
      class="block max-w-xl p-6 bg-white border border-gray-200 rounded-lg shadow hover:bg-gray-100 dark:bg-gray-800 dark:border-gray-700 dark:hover:bg-gray-700"
    >
      <h5 class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
        Hello tool bare component
      </h5>
    </div>
  );
}
