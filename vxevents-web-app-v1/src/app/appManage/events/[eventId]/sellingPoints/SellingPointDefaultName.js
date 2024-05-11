"use client";

import { useVxContext } from "@/app/context/VxContext";

export default function SellingPointDefaultName() {
  const { defaultSellingPointId, setDefaultSellingPointId } = useVxContext();
  console.log("defaultSellingPointId", defaultSellingPointId);

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
