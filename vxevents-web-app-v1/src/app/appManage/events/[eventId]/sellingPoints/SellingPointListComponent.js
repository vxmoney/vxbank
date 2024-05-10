"use client";
import Link from "next/link";
import React, { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { UserAuth } from "@/app/context/AuthContext";
import { useVxContext } from "@/app/context/VxContext";
import SellingPointUpdateModal from "./SellingPointUpdateModal";
import { publicEventProductAPI } from "@/api/publicEventProduct";

export default function SellingPointListComponent() {
  const { vxUserInfo } = UserAuth();
  const { eventId } = useParams();
  const { sellingPoints, fetchSellingPoints } = useVxContext();
  const [allProducts, setAllProducts] = useState([]);

  useEffect(() => {
    // all products
    publicEventProductAPI.getAll(vxUserInfo.vxToken, eventId).then((result) => {
      setAllProducts(result.data.productList);
    });
  }, [vxUserInfo, eventId]);

  useEffect(() => {
    // Check if vxToken is available before calling fetchEvents
    if (vxUserInfo && vxUserInfo?.vxToken) {
      fetchSellingPoints(vxUserInfo?.vxToken, eventId);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const formatPrice = (price) => {
    // Convert price to a string
    const priceStr = price.toString();
    // Insert a dot two characters from the end
    return priceStr.slice(0, -2) + "." + priceStr.slice(-2);
  };

  console.log("All products in list, ", allProducts);

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
              <td className="px-6 py-4">
                <SellingPointUpdateModal
                  pSellingPointId={sPoint.id}
                  pTitle={sPoint.title}
                  pSelectedProducts={sPoint.productList}
                  pAllProducts={allProducts}
                />
              </td>
              <td className="flex flex-wrap gap-2 px-6 py-4">
                {sPoint.productList.map((product) => (
                  <span
                    key={product.id}
                    className="bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-0.5 rounded-full dark:bg-blue-900 dark:text-blue-300"
                  >
                    {`${product.title} €${formatPrice(product.price)}`}
                  </span>
                ))}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
