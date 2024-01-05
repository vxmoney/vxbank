import React, { useEffect, useState } from 'react';
import { eventAPI } from '@/api/event';
import { UserAuth } from '../context/AuthContext';

export default function EventsComponent() {
  const [events, setEvents] = useState([]);
  const { vxUserInfo } = UserAuth();

  useEffect(() => {
    const vxToken = vxUserInfo?.vxToken;

    eventAPI.search(vxToken)
      .then(response => {
        console.log('API Response:', response.data);
        setEvents(response.data.eventList);
      })
      .catch(error => {
        console.error('API Error:', error);
        // Handle errors if needed
      });
  }, []);

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
        <tbody>
          {events.map(event => (
            <tr key={event.id} className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
              <td className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                {event.id}
              </td>
              <td className="px-6 py-4">
                {event.title}
              </td>
              <td className="px-6 py-4">
                {event.state}
              </td>
              <td className="px-6 py-4">
                {event.entryPrice}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
