import { pingAPI } from "@/api/ping";
import { userAPI } from "@/api/user";
import { useState } from "react";

export default function LocalAuthExample() {
  const initialMessage = "Please generate a random user";
  const initialLoginMessage = "Please login using the token generated above";

  const [pingResponse, setPingResponse] = useState(null);
  const [formattedResponse, setFormattedResponse] = useState(initialMessage);
  const [loginResponse, setLoginResponse] = useState(null);
  const [loginFormattedResponse, setLoginFormattedResponse] =
    useState(initialLoginMessage);

  const fetchGenerateFirebaseIdToken = async () => {
    try {
      const response = await pingAPI.generateFirebaseIdToken();
      const formattedResponse = JSON.stringify(response.data, null, 2);
      setPingResponse(response.data);
      setFormattedResponse(formattedResponse);
    } catch (error) {
      console.log("fetchPingError: ", error);
    }
  };

  const fetchLogin = async () => {
    try {
      const response = await userAPI.login(pingResponse.testFirebaseIdToken);
      const formattedResponse = JSON.stringify(response.data, null, 2);
      setLoginResponse(response.data);
      setLoginFormattedResponse(formattedResponse);
    } catch (error) {
      console.log("fetchPingError: ", error);
    }
  };

  return (
    <div className="p-4">
      <p class="mb-3 text-gray-500 dark:text-gray-400">
        This will work only if you are using localhost backend. It is useful to
        test the ui without having to deploy. It is very helpful for backend
        debuting.
      </p>

      <div class="grid grid-cols-4 gap-4 p-4">
        <div class="col-span-1">
          <p class="mb-4">Step 1: generate a random user</p>
          <button
            onClick={fetchGenerateFirebaseIdToken}
            class="bg-blue-500 text-white px-4 py-2 rounded"
          >
            Generate user
          </button>
        </div>

        <div class="col-span-3">
          <p>
            {formattedResponse && (
              <pre className="p-4" style={{ whiteSpace: "pre-wrap" }}>
                {formattedResponse}
              </pre>
            )}
          </p>
        </div>
      </div>

      <div class="grid grid-cols-4 gap-4 p-4">
        <div class="col-span-1">
          <p class="mb-4">Step 2: Login using this token</p>
          <button
            onClick={fetchLogin}
            class="bg-blue-500 text-white px-4 py-2 rounded"
          >
            Login
          </button>
        </div>

        <div class="col-span-3">
          <p>
            {loginFormattedResponse && (
              <pre className="p-4" style={{ whiteSpace: "pre-wrap" }}>
                {loginFormattedResponse}
              </pre>
            )}
          </p>
        </div>
      </div>
    </div>
  );
}
