import React, { useEffect, useState } from "react";
import { UserAuth } from "../../context/AuthContext";
import { useVxContext } from "../../context/VxContext";
import Link from "next/link";

export default function PublicEventListComponent() {
  const { events, fetchEvents } = useVxContext();

  const { vxUserInfo } = UserAuth();

  useEffect(() => {
    // Check if vxToken is available before calling fetchEvents
    if (vxUserInfo && vxUserInfo?.vxToken) {
      fetchEvents(vxUserInfo?.vxToken, vxUserInfo?.id);
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
              State
            </th>
            <th scope="col" className="px-6 py-3">
              Entry Price
            </th>
          </tr>
        </thead>
        <tbody>
          {events.map((event) => (
            <tr
              key={event.id}
              className="bg-white border-b dark:bg-gray-800 dark:border-gray-700"
            >
              <td className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                {event.id}
              </td>
              <td className="px-6 py-4">
                <Link href={`/appManage/events/${event.id}`}>
                  {event.title}
                </Link>
              </td>
              <td className="px-6 py-4">{event.state}</td>
              <td className="px-6 py-4">{event.entryPrice}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
