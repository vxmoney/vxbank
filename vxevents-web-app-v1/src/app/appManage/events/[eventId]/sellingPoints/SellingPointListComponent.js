"use client";
import Link from "next/link";
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

  console.log("sellingPoints DEBUG 1", sellingPoints);

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
          {sellingPoints.map((sPoint) => (
            <tr
              key={sPoint.id}
              className="bg-white border-b dark:bg-gray-800 dark:border-gray-700"
            >
              <td className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                {sPoint.id}
              </td>
              <td className="px-6 py-4">{sPoint.title}</td>
              <td className="px-6 py-4"> list of products goes here</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
