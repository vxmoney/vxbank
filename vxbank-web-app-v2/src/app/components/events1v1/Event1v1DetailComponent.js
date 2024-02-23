"use client";
import { useParams } from "next/navigation";
import { useState, useEffect } from "react";
import { eventAPI } from "@/api/event";
import { eventParticipantAPI } from "@/api/eventParticipant";
import { eventResultsAPI } from "@/api/eventResultEndpoint";
import { UserAuth } from "../../context/AuthContext";
import Join1v1EventModal from "./Join1v1EventModal";
import Event1v1ParticipantListComponent from "./Event1v1ParticipantListComponent";

export default function Event1v1DetailComponent() {
  const { vxUserInfo } = UserAuth();
  let { eventId } = useParams();
  const [eventData, setEventData] = useState(null);
  const [participantResponse, setParticipantsResponse] = useState(null);
  const [resultsData, setResultsData] = useState(null);

  // fetch event
  const fetchEvent = async () => {
    try {
      const response = await eventAPI.getById(vxUserInfo?.vxToken, eventId);
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

  // fetchParticipants
  const fetchParticipants = async () => {
    try {
      const response = await eventParticipantAPI.getByEventId(
        vxUserInfo?.vxToken,
        eventId
      );
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

  // fetch results
  const fetchResults = async () => {
    try {
      const response = await eventResultsAPI.getByEventId(
        vxUserInfo?.vxToken,
        eventId
      );
      setResultsData(response.data);
    } catch (error) {
      console.error("Error fetching results:", error);
    }
  };
  
  useEffect(() => {
    if (eventId && vxUserInfo && vxUserInfo.vxToken) {
      fetchResults();
    }
  }, []);

  return (
    <div>
      {eventData && (
        <div>
          {/* Event details */}
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
                      Entry price
                    </th>
                    <td className="px-6 py-4">
                      {eventData.entryPrice} {eventData.currency}
                    </td>
                  </tr>
                  <tr className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
                    <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                      Game
                    </th>
                    <td className="px-6 py-4">{eventData.vxGame}</td>
                  </tr>
                  <tr className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
                    <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                      Event type
                    </th>
                    <td className="px-6 py-4">{eventData.type}</td>
                  </tr>
                  <tr className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
                    <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                      State
                    </th>
                    <td className="px-6 py-4">{eventData.state}</td>
                  </tr>
                  <tr className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
                    <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                      Available funds
                    </th>
                    <td className="px-6 py-4">{eventData.availableFunds}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            {/* Join section */}
          </div>
        </div>
      )}

      <Event1v1ParticipantListComponent
        eventId={eventId}
        vxUserList={
          participantResponse && participantResponse.vxUserList
        }
      />

      <Join1v1EventModal fetchParticipants={fetchParticipants}/>
    </div>
  );
}
