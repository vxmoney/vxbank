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
        setSellingPoint(result.data);
      });
  }, [defaultSellingPointId]);


  // if defaultSellingPointId is not set return
  if (sellingPoint === null) {
    return (
      <div>
        <h1>No default selling point</h1>
      </div>
    );
  }

  return (
    <div>
      <h1>Default: {sellingPoint.title}</h1>
    </div>
  );
}
