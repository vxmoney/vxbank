"use client";

import { useState, useEffect } from "react";
import { useVxContext } from "@/app/context/VxContext";
import { publicEventSellingPointAPI } from "@/api/publicEventSellingPoint";
import { UserAuth } from "@/app/context/AuthContext";
import { useSellContext } from "@/app/context/SellContext";
import SellProductButtonComponent from "./SellProductButtonComponent";

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

  let buttonList = null;
  if (sellingPoint && sellingPoint.productList) {
    buttonList = sellingPoint.productList.map((product) => (
      <SellProductButtonComponent
        product={product}
        key={product.id}
        className="flex flex-col items-center justify-center"
      />
    ));
  }

  return (
    <div className="block rounded-lg ">
      <div className="p-2 bg-white dark:bg-gray-800 rounded-lg">
        <dl className="grid max-w-screen-xl grid-cols-3 mx-auto text-gray-900 sm:grid-cols-3 xl:grid-cols-6 dark:text-white sm:p-8">
          {buttonList}
        </dl>
      </div>
    </div>
  );
}
