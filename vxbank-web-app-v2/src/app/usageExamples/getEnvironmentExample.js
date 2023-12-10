import { pingAPI } from "@/api/ping";
import { useState } from "react";

export default function GetEnvironmentExample() {
  const [pingResponse, setPingResponse] = useState(null);

  const fetchPing = async () => {
    try {
      const response = await pingAPI.getEnvironment();
      setPingResponse(response.data);
    } catch (error) {
      console.log("fetchPingError: ", error);
    }
  };

  return (
    <div class="text-sm font-medium text-center text-gray-500 border-b border-gray-200 dark:text-gray-400 dark:border-gray-700">
      <div>Get environment example hello</div>
      <button onClick={fetchPing}>Fetch ping</button>
      {pingResponse && <div>{pingResponse.environment}</div>}
    </div>
  );
}
