"use client"
import React, { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { UserAuth } from "@/app/context/AuthContext";
import { useVxContext } from "@/app/context/VxContext";

export default function SellingPointListComponent() {

    const { vxUserInfo } = UserAuth();
    const { eventId } = useParams();
    const { sellingPoints, fetchSellingPoints } = useVxContext();

    useEffect(() => {
        // Check if vxToken is available before calling fetchEvents
        if (vxUserInfo && vxUserInfo?.vxToken) {
          fetchSellingPoints(vxUserInfo?.vxToken, eventId);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
      }, []);

  return (
    <div className="p-2 relative overflow-x-auto">
      <table className="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400">
        <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
          <tr>
            <th scope="col" className="px-6 py-3">
              ID
            </th>
            <th scope="col" className="px-6 py-3">
              Title
            </th>
            <th scope="col" className="px-6 py-3">
              Products
            </th>
            
          </tr>
        </thead>
        <tbody>
          
        </tbody>
      </table>
    </div>
);
}
