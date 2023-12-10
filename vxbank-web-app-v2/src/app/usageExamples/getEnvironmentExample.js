import { pingAPI } from "@/api/ping";
import { useState } from "react";

export default function GetEnvironmentExample() {
  const [pingResponse, setPingResponse] = useState(null);
  const [formattedResponse, setFormattedResponse] = useState(null);

  const fetchPing = async () => {
    try {
      const response = await pingAPI.getEnvironment();
      const formattedResponse = JSON.stringify(response.data, null, 2);
      setPingResponse(response.data);
      setFormattedResponse(formattedResponse);
    } catch (error) {
      console.log("fetchPingError: ", error);
    }
  };

  return (
    <div>
      <div>Get environment example hello</div>
      <button
        onClick={fetchPing}
        type="button"
        class="text-white bg-gray-800 hover:bg-gray-900 focus:outline-none focus:ring-4 focus:ring-gray-300 font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-gray-800 dark:hover:bg-gray-700 dark:focus:ring-gray-700 dark:border-gray-700"
      >
        Fetch ping
      </button>
      {formattedResponse && <pre>{formattedResponse}</pre>}
    </div>
  );
}
