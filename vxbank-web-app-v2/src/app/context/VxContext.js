import React, { useState, useEffect, useContext, createContext } from 'react';
import { eventAPI } from '@/api/event';


const VxContext = createContext();

export const useVxContext = () => {
  return useContext(VxContext);
};

export const VxProvider = ({ children }) => {
  const [events, setEvents] = useState([]);

  const fetchEvents = (vxToken) => {
    if (!vxToken) {
      console.error('VxToken is required for fetching events.');
      return;
    }

    eventAPI.search(vxToken)
      .then(response => {
        console.log('VxContext API Response:', response.data);
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
