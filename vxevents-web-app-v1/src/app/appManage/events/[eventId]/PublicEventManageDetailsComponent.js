"use client";
import { useParams } from "next/navigation";
import { useState, useEffect } from "react";
import { UserAuth } from "@/app/context/AuthContext";
import { publicEventAPI } from "@/api/publicEvent";

export default function PublicEventManageDetailsComponent() {
  const { vxUserInfo } = UserAuth();
  const { eventId } = useParams();

  const [eventData, setEventData] = useState(null);

  // fetch event
  const fetchEvent = async () => {
    try {
      const response = await publicEventAPI.getById(
        vxUserInfo?.vxToken,
        eventId
      );
      console.log("response", response.data);
      setEventData(response.data);
    } catch (error) {
      console.error("Error fetching event:", error);
    }
  };

  useEffect(() => {
    if (eventId && vxUserInfo && vxUserInfo.vxToken) {
      fetchEvent();
    }
  }, []);

  return (
    <div>
      {/* Event details */}
      {eventData && (
        <div className="pl-8 pr-8 pt-8">
          <div className="block  p-6 bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700 ">
            <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
              Event details
            </h5>

            <table className="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400">
              <tbody>
                <tr className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
                  <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                    Event title
                  </th>
                  <td className="px-6 py-4">{eventData.title}</td>
                </tr>
                <tr className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
                  <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                    Currency
                  </th>
                  <td className="px-6 py-4">{eventData.currency}</td>
                </tr>
              </tbody>
            </table>
            <h6 className="pt-8 mb-2 text-1xl font-bold tracking-tight text-gray-900 dark:text-white">
              Other this to be implemented
            </h6>
            <p class="font-normal text-gray-700 dark:text-gray-400">
              How QR code for clients to login
            </p>
            <p class="font-normal text-gray-700 dark:text-gray-400">
              Show QR code for clients to self pay
            </p>
          </div>
        </div>
      )}
    </div>
  );
}
