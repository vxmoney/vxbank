"use client";
import { useSellContext } from "@/app/context/SellContext";
import { MdOutlineSettings } from "react-icons/md";
import { BiReset } from "react-icons/bi";
import { FaCheckDouble } from "react-icons/fa";
import { useParams } from "next/navigation";
import { useVxContext } from "@/app/context/VxContext";
import { publicEventClientPaymentAPI } from "@/api/publicEventClientPayment";
import { UserAuth } from "@/app/context/AuthContext";

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

  let { eventId, clientId } = useParams();
  const { defaultSellingPointId } = useVxContext();
  const { vxUserInfo } = UserAuth();

  const switchToolBar = () => {
    setShowToolBar(!showToolBar);
  };

  const handleReset = () => {
    resetSelectedItems();
    setAddItems(true);
  };

  // Function to compute the total price of all items in the sellItemList array
  function computeTotalPrice(items) {
    return items.reduce((total, item) => {
      return total + item.price;
    }, 0);
  }

  // function to build order item list
  function buildOrderItemParamsList(sellItemList) {
    const orderItemMap = new Map();

    // Iterate through each item in the sell item list
    sellItemList.forEach((item) => {
      const itemId = item.id;
      const price = item.price;

      // Check if the event ID is already in the map
      if (orderItemMap.has(itemId)) {
        const orderItem = orderItemMap.get(itemId);
        // Update the quantity and value
        orderItem.quantity += 1;
        orderItem.value += price;
      } else {
        // Add new entry in the map
        orderItemMap.set(itemId, {
          vxPublicEventProductId: itemId,
          quantity: 1,
          value: price,
        });
      }
    });

    // Convert the map values to a list
    return Array.from(orderItemMap.values());
  }

  const formatPrice = (price) => `${(price / 100).toFixed(2)}`;

  // section with important relevant param field values
  const value = computeTotalPrice(sellItemList);
  const orderItemParamsList = buildOrderItemParamsList(sellItemList);
  const vxPublicEventSellingPointId = defaultSellingPointId;

  const ManagerRegistersPaymentParams = {
    eventId,
    clientId,
    value,
    vxPublicEventSellingPointId,
    orderItemParamsList,
  };
  console.log("ManagerRegistersPaymentParams", ManagerRegistersPaymentParams);

  const handleSubmit = () => {
    publicEventClientPaymentAPI
      .managerRegistersPayment(
        vxUserInfo.vxToken,
        ManagerRegistersPaymentParams
      )
      .then((response) => {
        console.log("Payment registered:", response.data);
        resetSelectedItems();
        setAddItems(true);
        displayToast();
      })
      .catch((error) => {
        console.error("Error registering payment:", error);
      });
  };

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
        {formatPrice(value)} Euro
      </h5>
      {okButton}
    </div>
  );
}
