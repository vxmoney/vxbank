"use client"
import { useParams } from "next/navigation";
import { useState, useEffect } from "react";
import { eventAPI } from "@/api/event";

export default function LeagueOfLegends1v1Component(){

    let { eventId } = useParams();
    const [eventData, setEventData] = useState(null);

    useEffect(() => {
        const fetchEvent = async () => {
          try {
            const response = await eventAPI.getById(eventId);
            console.log("event data: ", response.data)
            setEventData(response.data);
          } catch (error) {
            console.error("Error fetching event:", error);
          }
        };
    
        if (eventId) {
          fetchEvent();
        }
      }, [eventId]);

    return (
        <di>
            <h1>
                Hello nested id = : {eventId} 
            </h1>
        </di>
    )
}