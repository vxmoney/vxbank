"use client";
import { useParams } from "next/navigation";
import { useState, useEffect } from "react";
import { UserAuth } from "@/app/context/AuthContext";
import { publicEventAPI } from "@/api/publicEvent";

export default function PublicEventManageComponent() {
  const { vxUserInfo } = UserAuth();
  const { eventId } = useParams();

  // fetch event
  const fetchEvent = async () => {
    try {
      const response = await publicEventAPI.getById(vxUserInfo?.vxToken, eventId);
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
      <h1>Event ID: {eventId}</h1>
    </div>
  );
}
