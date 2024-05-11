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
      <div key={product.id} class="p-4 m-4 flex flex-col items-center justify-center">
        <div class=" text-3xl font-extrabold whitespace-nowrap">{product.title}</div>
      </div>
    ));
  }

  return (
    <div class="block max-w-xl rounded-lg ">
      <div
        class="bg-white dark:bg-gray-800"
        id="stats"
        role="tabpanel"
        aria-labelledby="stats-tab"
      >

        <dl class="grid max-w-screen-xl grid-cols-2 gap-8 p-4 mx-auto text-gray-900 sm:grid-cols-3 xl:grid-cols-6 dark:text-white sm:p-8">
          {productList}
        </dl>
      </div>
    </div>
  );
}
