"use client";
import { useParams } from "next/navigation";
import { useState, useEffect } from "react";
import { eventAPI } from "@/api/event";
import { eventParticipantAPI } from "@/api/eventParticipant";
import { UserAuth } from "../../context/AuthContext";

export default function Event1v1ParticipantListComponent({ eventId }) {
  const { vxUserInfo } = UserAuth();

  console.log("eventId = ", eventId);

  const {participantsList, setParticipantsList} = useState(null)

  useEffect(() => {
    const fetchParticipants = async () => {
      try {
        const response = await eventParticipantAPI.getByEventId(
          vxUserInfo?.vxToken,
          eventId
        );
        console.log("eventParticipantResponse, ", response.data);
        //setEventData(response.data);
        setParticipantsList(response.data)
      } catch (error) {
        console.error("Error fetching event:", error);
      }
    };

    if (eventId && vxUserInfo && vxUserInfo?.vxToken) {
      fetchParticipants();
    }
  }, []);

  useEffect(() => {
    console.log("participantsList", participantsList);
  }, [participantsList]);

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
        {/* <tbody>
          {events.map((event) => (
            <tr
              key={event.id}
              className="bg-white border-b dark:bg-gray-800 dark:border-gray-700"
            >
              <td className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                {event.id}
              </td>
              <td className="px-6 py-4">
                <Link href={`/events/leagueOfLegends/${event.id}`}>
                  {event.title}
                </Link>
              </td>
              <td className="px-6 py-4">{event.state}</td>
              <td className="px-6 py-4">{event.entryPrice}</td>
            </tr>
          ))}
        </tbody> */}
      </table>
    </div>
  );
}
