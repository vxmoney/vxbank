"use client";

import { useState, useEffect } from "react";
import { useVxContext } from "@/app/context/VxContext";
import { publicEventSellingPointAPI } from "@/api/publicEventSellingPoint";
import { UserAuth } from "@/app/context/AuthContext";
import { useSellContext } from "@/app/context/SellContext";

export default function SellProductListComponent() {
  const { vxUserInfo } = UserAuth();
  const { defaultSellingPointId, setDefaultSellingPointId } = useVxContext();
  const [sellingPoint, setSellingPoint] = useState(null);
  const { sellItemList } = useSellContext();

  const getDefaultSellingPointId = () => {
    if (typeof defaultSellingPointId !== "number") {
      return parseInt(defaultSellingPointId);
    }
    return defaultSellingPointId;
  };

  useEffect(() => {
    if (defaultSellingPointId === "null") {
      return;
    }
    publicEventSellingPointAPI
      .get(vxUserInfo.vxToken, getDefaultSellingPointId())
      .then((result) => {
        setSellingPoint(result.data);
      });
  }, [defaultSellingPointId]);

  let productList = null;
  if (sellingPoint && sellingPoint.productList) {
    productList = sellingPoint.productList.map((product) => (
      <div
        key={product.id}
        class="flex flex-col items-center justify-center"
      >
        <button
          type="button"
          class="text-gray-900 bg-white border border-gray-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-full text-sm px-5 py-2.5 me-2 mb-2 dark:bg-gray-800 dark:text-white dark:border-gray-600 dark:hover:bg-gray-700 dark:hover:border-gray-600 dark:focus:ring-gray-700 whitespace-nowrap"
        >
          {product.title}
        </button>
      </div>
    ));
  }

  return (
    <div class="block rounded-lg ">
      <div
        class="bg-white dark:bg-gray-800 rounded-lg"
        
      >
        <dl class="grid max-w-screen-xl grid-cols-3 mx-auto text-gray-900 sm:grid-cols-3 xl:grid-cols-6 dark:text-white sm:p-8">
          {productList}
        </dl>
      </div>
    </div>
  );
}
