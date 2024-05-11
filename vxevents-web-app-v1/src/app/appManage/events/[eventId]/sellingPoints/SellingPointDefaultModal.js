"use client";

import { useVxContext } from "@/app/context/VxContext";

export default function SellingPointDefaultModal({ sellingPointId, title }) {
  const { defaultSellingPointId, setDefaultSellingPointId } = useVxContext();
  console.log("defaultSellingPointId", defaultSellingPointId);

  return (
    <div> Modal {title}    </div>
  );
}
