import { pingAPI } from "@/api/ping";
import { userAPI } from "@/api/user";
import { useState } from "react";
import { UserAuth } from "../../context/AuthContext";

export default function AppEngineAuthExample() {
  const { user, setVxToken, vxToken, vxUserInfo, setVxUserInfo } = UserAuth();

  const initialMessage =
    "Not needed. Check the top bar to see if you are logged in";
  const initialLoginMessage = "Please login using the real firebase token";
  const whoAmIMessage =
    "Who am I?. To find out call the backend and pass the vxToken";

  const [pingResponse, setPingResponse] = useState(null);
  const [formattedResponse, setFormattedResponse] = useState(initialMessage);
  const [loginResponse, setLoginResponse] = useState(null);
  const [loginFormattedResponse, setLoginFormattedResponse] =
    useState(initialLoginMessage);
  const [whoAmIResponse, setWhoAmIResponse] = useState(null);
  const [whoAmIFormattedResponse, setWhoAmIFormattedResponse] =
    useState(whoAmIMessage);

  const fetchLogin = async () => {
    try {
      const response = await userAPI.login(user.accessToken);
      const formattedResponse = JSON.stringify(response.data, null, 2);
      setLoginResponse(response.data);
      setLoginFormattedResponse(formattedResponse);
      setVxUserInfo(response.data);
    } catch (error) {
      console.log("fetchLoginError: ", error);
    }
  };

  const fetchPingWhoAmI = async () => {
    try {
      const response = await pingAPI.whoAmI(vxUserInfo.vxToken);
      const formattedResponse = JSON.stringify(response.data, null, 2);
      setWhoAmIResponse(response.data);
      setWhoAmIFormattedResponse(formattedResponse);
    } catch (error) {
      console.log("fetchPingWhoAmIError: ", error);
    }
  };

  const callSetVxToken = async () => {
    try {
      setVxToken(loginResponse.vxToken);
    } catch (error) {
      console.log("callSetVxToken error", error);
    }
  };

  return (
    <div className="p-4">
      <p className="mb-3 text-gray-500 dark:text-gray-400">
        This connects to appengine. No need to generate fake token. You can use
        the real firebase id to swap for a vxToken.
      </p>

      <div className="grid grid-cols-4 gap-4 p-4">
        <div className="col-span-1">
          <p className="mb-4">Step 1: generate a random user</p>
        </div>

        <div className="col-span-3">
          <p>
            {formattedResponse && (
              <pre className="p-4" style={{ whiteSpace: "pre-wrap" }}>
                {formattedResponse}
              </pre>
            )}
          </p>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4 p-4">
        <div className="col-span-1">
          <p className="mb-4">Step 2: Login using this token</p>
          <button
            onClick={fetchLogin}
            className="bg-blue-500 text-white px-4 py-2 rounded"
          >
            Login
          </button>
        </div>

        <div className="col-span-3">
          <p>
            {loginFormattedResponse && (
              <pre className="p-4" style={{ whiteSpace: "pre-wrap" }}>
                {loginFormattedResponse}
              </pre>
            )}
          </p>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4 p-4">
        <div className="col-span-1">
          <p className="mb-4">Step 3: Find you who am I using the vxToken</p>
          <button
            onClick={fetchPingWhoAmI}
            className="bg-blue-500 text-white px-4 py-2 rounded"
          >
            Who am I?
          </button>
        </div>

        <div className="col-span-3">
          <div>
            {whoAmIFormattedResponse && (
              <pre className="p-4" style={{ whiteSpace: "pre-wrap" }}>
                {whoAmIFormattedResponse}
              </pre>
            )}
          </div>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4 p-4">
        <div className="col-span-1">
          <p className="mb-4">Step 3: Find you who am I using the vxToken</p>
          <button
            onClick={callSetVxToken}
            className="bg-blue-500 text-white px-4 py-2 rounded"
          >
            Set vxToken
          </button>
        </div>

        <div className="col-span-3">
          <p>vxToken = {vxToken}</p>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4 p-4">
        <div className="col-span-1">
          <p className="mb-4">decoded vxUserInfo from context</p>
        </div>

        <div className="col-span-3">
          <p>
            {vxUserInfo && (
              <div>
                <pre className="p-4" style={{ whiteSpace: "pre-wrap" }}>
                  {JSON.stringify(vxUserInfo, null, 2)}
                </pre>
              </div>
            )}
          </p>
        </div>
      </div>
    </div>
  );
}
