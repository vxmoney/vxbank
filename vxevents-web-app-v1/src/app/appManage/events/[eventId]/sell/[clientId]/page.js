"use client";
import { useParams } from "next/navigation";
import { useState, useEffect } from "react";
import { useVxContext } from "@/app/context/VxContext";
import { publicEventSellingPointAPI } from "@/api/publicEventSellingPoint";
import { UserAuth } from "@/app/context/AuthContext";
import SellProductListComponent from "./SellProductListComponent";
import SellTotalComponent from "./SellTotalComponent";
import SellToolBarComponent from "./SellToolBarComponent";

export default function SellManagePage() {
  let { eventId, clientId } = useParams();
  const { vxUserInfo } = UserAuth();
  const { defaultSellingPointId, setDefaultSellingPointId } = useVxContext();
  const [sellingPoint, setSellingPoint] = useState(null);
  

  // create a method that return default selling point id but as a number that passes this check typeof pointId !== 'number'
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

  return (
    <div className="flex flex-col items-center justify-center">
      <SellTotalComponent />
      <SellToolBarComponent />
      <SellProductListComponent />
      <div>
        <div>Hello sell page</div>
        <div>Event ID: {eventId}</div>
        <div>Client ID: {clientId}</div>
        <div>Default selling point id: {defaultSellingPointId}</div>
      </div>
    </div>
  );
}
