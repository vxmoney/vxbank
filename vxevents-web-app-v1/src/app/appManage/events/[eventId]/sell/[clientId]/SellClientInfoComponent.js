"use client";
import { useState, useEffect } from "react";
import { useParams } from "next/navigation";
import { UserAuth } from "@/app/context/AuthContext";
import { publicEventClientPaymentAPI } from "@/api/publicEventClientPayment";

export default function SellClientInfoComponent() {
  let { eventId, clientId } = useParams();
  let { vxUserInfo } = UserAuth();

  let [clientReport, setClientReport] = useState();

  useEffect(() => {
    if (vxUserInfo) {
      publicEventClientPaymentAPI
        .getClientReport(vxUserInfo.vxToken, eventId, clientId)
        .then((res) => {
          setClientReport(res.data);
        });
    }
  }, [vxUserInfo]);

  const formatPrice = (price) => `${(price / 100).toFixed(2)}`;

  console.log(clientReport);

  return (
    <div className="flex-col gap-4 items-center block max-w-xl p-6 bg-white border border-gray-200 rounded-lg shadow hover:bg-gray-100 dark:bg-gray-800 dark:border-gray-700 dark:hover:bg-gray-700">
      <div>Client id: {clientReport?.vxPublicEventClientId}</div>
      <div>Email: {clientReport?.clientEmail}</div>
      <div>Available funds: {formatPrice(clientReport?.availableBalance)}</div>
    </div>
  );
}
