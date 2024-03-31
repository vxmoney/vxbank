import React, { useState, useEffect, useContext, createContext } from 'react';
import { eventAPI } from '@/api/event';
import { publicEventAPI } from '@/api/publicEvent';


const VxContext = createContext();

export const useVxContext = () => {
  return useContext(VxContext);
};

export const VxProvider = ({ children }) => {
  const [events, setEvents] = useState([]);

  const fetchEvents = (vxToken,vxUserId) => {
    if (!vxToken) {
      console.error('VxToken is required for fetching events.');
      return;
    }

    eventAPI.search(vxToken)
      .then(response => {
        setEvents(response.data.eventList);
      })
      .catch(error => {
        console.error('API Error:', error);
        // Handle errors if needed
      });
  };

  const value = {
    events,
    fetchEvents,
  };

  return (
    <VxContext.Provider value={value}>
      {children}
    </VxContext.Provider>
  );
};
