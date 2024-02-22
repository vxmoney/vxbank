"use client";
import { useParams } from "next/navigation";
import { useState, useEffect } from "react";
import { eventAPI } from "@/api/event";
import { UserAuth } from "../context/AuthContext";

export default function LeagueOfLegends1v1Component() {
  const { vxUserInfo } = UserAuth();
  let { eventId } = useParams();
  const [eventData, setEventData] = useState(null);

  useEffect(() => {
    const fetchEvent = async () => {
      try {
        const response = await eventAPI.getById(vxUserInfo?.vxToken, eventId);
        console.log("event data: ", response.data);
        setEventData(response.data);
      } catch (error) {
        console.error("Error fetching event:", error);
      }
    };

    if (eventId && vxUserInfo && vxUserInfo?.vxToken) {
      fetchEvent();
    }
  }, [eventId]);

  return (
    <di>
      <h1>Hello nested id = : {eventId}</h1>
    </di>
  );
}
