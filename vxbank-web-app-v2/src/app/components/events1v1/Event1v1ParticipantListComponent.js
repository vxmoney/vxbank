"use client";
import { useParams } from "next/navigation";
import { useState, useEffect } from "react";
import { eventAPI } from "@/api/event";
import { eventParticipantAPI } from "@/api/eventParticipant";
import { UserAuth } from "../../context/AuthContext";

export default function Event1v1ParticipantListComponent({ eventId, vxUserList }) {
  const { vxUserInfo } = UserAuth();

  console.log("eventId = ", eventId);

  const [participantResponse, setParticipantsResponse] = useState(null);

  const fetchParticipants = async () => {
    try {
      const response = await eventParticipantAPI.getByEventId(
        vxUserInfo?.vxToken,
        eventId
      );
      console.log("eventParticipantResponse, ", response.data);
      setParticipantsResponse(response.data);
    } catch (error) {
      console.error("Error fetching participants:", error);
    }
  };

  useEffect(() => {
    if (eventId && vxUserInfo && vxUserInfo.vxToken) {
      fetchParticipants();
    }
  }, [eventId]);

 
    console.log("vxUserList", vxUserList);

  return (
    <div className="pl-8 pr-8 pt-8">
      <div className="block  p-6 bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700 ">
        <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
          Participant list
        </h5>
        <table className="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400">
          <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
            <tr>
              <th scope="col" className="px-6 py-3">
                gamerId
              </th>
              <th scope="col" className="px-6 py-3">
                name
              </th>
            </tr>
          </thead>
          {vxUserList && (
            <tbody>
              {vxUserList.map((gamer) => (
                <tr
                  key={gamer.id}
                  className="bg-white border-b dark:bg-gray-800 dark:border-gray-700"
                >
                  <td className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                    ---
                  </td>
                  <td className="px-6 py-4">{gamer.name}</td>
                </tr>
              ))}
            </tbody>
          )}
        </table>
      </div>
    </div>
  );
}
