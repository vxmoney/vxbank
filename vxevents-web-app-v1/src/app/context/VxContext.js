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
        console.log("result");
        console.log(response);
        setEvents(response.data.eventList);
      })
      .catch((error) => {
        console.error("API Error:", error);
        // Handle errors if needed
      });
  };

  const fetchSellingPoints = (vxToken, vxUserId) => {
    if (!vxToken){
      console.error("VxToken is required for fetching sellingPoints.");
      return;
    }
    

  }

  const value = {
    events,
    fetchEvents,
  };

  return <VxContext.Provider value={value}>{children}</VxContext.Provider>;
};
