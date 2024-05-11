"use client";

import { useState, useEffect } from "react";
import { useVxContext } from "@/app/context/VxContext";
import { publicEventSellingPointAPI } from "@/api/publicEventSellingPoint";
import { UserAuth } from "@/app/context/AuthContext";

export default function SellingPointDefaultName() {
  const { vxUserInfo } = UserAuth();
  const { defaultSellingPointId, setDefaultSellingPointId } = useVxContext();
  const [sellingPoint, setSellingPoint] = useState(null);

  // create a method that return default selling point id but as a number that passes this check typeof pointId !== 'number'
  const getDefaultSellingPointId = () => {
    if (typeof defaultSellingPointId !== "number") {
      return parseInt(defaultSellingPointId);
    }
    return defaultSellingPointId;
  }



  useEffect(() => {
    if (defaultSellingPointId === "null") {
      return;
    }
    publicEventSellingPointAPI
      .get(vxUserInfo.vxToken, getDefaultSellingPointId())
      .then((result) => {
        console.log("DEBUG result data", result.data);
        setSellingPoint(result.data);
      });
  }, [defaultSellingPointId]);

  console.log("DEBUG sellingPoint value", sellingPoint);

  // if defaultSellingPointId is not set return
  if (defaultSellingPointId === "null") {
    return (
      <div>
        <h1>No default selling point</h1>
      </div>
    );
  }

  return (
    <div>
      <h1>Default selling point id: {defaultSellingPointId}</h1>
    </div>
  );
}
