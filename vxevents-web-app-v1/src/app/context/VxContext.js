import React, { useState, useEffect, useContext, createContext } from "react";
import { eventAPI } from "@/api/event";
import { publicEventAPI } from "@/api/publicEvent";
import { publicEventSellingPointAPI } from "@/api/publicEventSellingPoint";

const VxContext = createContext();

export const useVxContext = () => {
  return useContext(VxContext);
};

export const VxProvider = ({ children }) => {
  const [events, setEvents] = useState([]);
  const [sellingPoints, setSellingPoints] = useState([]);

  const fetchEvents = (vxToken, vxUserId) => {
    if (!vxToken) {
      console.error("VxToken is required for fetching events.");
      return;
    }

    publicEventAPI
      .search(vxToken, vxUserId)
      .then((response) => {
        setEvents(response.data.eventList);
      })
      .catch((error) => {
        console.error("API Error:", error);
        // Handle errors if needed
      });
  };

  const fetchSellingPoints = (vxToken, publicEventId) => {
    if (!vxToken) {
      console.error("VxToken is required for fetching sellingPoints.");
      return;
    }
    publicEventSellingPointAPI
      .getAll(vxToken, publicEventId)
      .then((response) => {
        setSellingPoints(response.data.sellingPointList);
      })
      .catch((error) => {
        console.error("Error publicEventSellingPointAPI.getAll:", error);
      });
  };

  const value = {
    events,
    fetchEvents,
    sellingPoints,
    fetchSellingPoints
  };

  return <VxContext.Provider value={value}>{children}</VxContext.Provider>;
};
