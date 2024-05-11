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
      <div class="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
        <div class="flex">
          <div class="flex items-center me-4">
            <input
              id="inline-radio"
              type="radio"
              value=""
              name="inline-radio-group"
              class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2 dark:bg-gray-700 dark:border-gray-600"
            />{" "}
            <label
              for="inline-radio"
              class="ms-2 text-sm font-medium text-gray-900 dark:text-gray-300"
            >
              Inline 1
            </label>
          </div>
          <div class="flex items-center me-4">
            <input
              id="inline-2-radio"
              type="radio"
              value=""
              name="inline-radio-group"
              class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2 dark:bg-gray-700 dark:border-gray-600"
            />
            <label
              for="inline-2-radio"
              class="ms-2 text-sm font-medium text-gray-900 dark:text-gray-300"
            >
              Inline 2
            </label>
          </div>
          <div class="flex items-center me-4">
            <input
              checked
              id="inline-checked-radio"
              type="radio"
              value=""
              name="inline-radio-group"
              class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2 dark:bg-gray-700 dark:border-gray-600"
            />
            <label
              for="inline-checked-radio"
              class="ms-2 text-sm font-medium text-gray-900 dark:text-gray-300"
            >
              Inline checked
            </label>
          </div>
        </div>
      </div>
    </div>
  );
}
