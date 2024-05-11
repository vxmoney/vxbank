"use client";
import { useSellContext } from "@/app/context/SellContext";

export default function SellToolBarComponent() {
  const { addItems, setAddItems } = useSellContext();

  // Function to handle radio change
  const handleRadioChange = (value) => {
    setAddItems(value === "true"); // Parse the string value to boolean and update addItems
  };

  console.log(addItems);

  return (
    <div
      className="block max-w-xl p-6 bg-white border border-gray-200 rounded-lg shadow hover:bg-gray-100 dark:bg-gray-800 dark:border-gray-700 dark:hover:bg-gray-700"
    >
      <div className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">

        
        <div className="flex">
          <div className="flex items-center me-4">
            <input
              id="inline-radio"
              type="radio"
              value="true"
              name="inline-radio-group"
              className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2 dark:bg-gray-700 dark:border-gray-600"
              checked={addItems === true}
              onChange={() => handleRadioChange("true")}
            />
            <label
              htmlFor="inline-radio"
              className="ms-2 text-sm font-medium text-gray-900 dark:text-gray-300"
            >
              Inline 1
            </label>
          </div>
          <div className="flex items-center me-4">
            <input
              id="inline-2-radio"
              type="radio"
              value="false"
              name="inline-radio-group"
              className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2 dark:bg-gray-700 dark:border-gray-600"
              checked={addItems === false}
              onChange={() => handleRadioChange("false")}
            />
            <label
              htmlFor="inline-2-radio"
              className="ms-2 text-sm font-medium text-gray-900 dark:text-gray-300"
            >
              Inline 2
            </label>
          </div>
        </div>
      </div>
    </div>
  );
}
