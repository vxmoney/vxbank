import { pingAPI } from "@/api/ping";
import { userAPI } from "@/api/user";
import { useState } from "react";
import { UserAuth } from "../context/AuthContext";

export default function LocalAuthExample() {
  const {
    user,
    googleSignIn,
    logOut,
    setVxToken,
    vxToken,
    vxUserInfo,
    setVxUserInfo,
  } = UserAuth();

  const initialMessage = "Please generate a random user";
  const initialLoginMessage = "Please login using the token generated above";
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
      setVxUserInfo(response.data);
    } catch (error) {
      console.log("fetchLoginError: ", error);
    }
  };

  const fetchPingWhoAmI = async () => {
    try {
      const response = await pingAPI.whoAmI(loginResponse.vxToken);
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

  const callSetVxUserInfo = async () => {
    try {
      setVxUserInfo(loginResponse);
    } catch (error) {
      console.log("callSetVxUserInfo error", error);
    }
  };

  const generateUserAndLgoIn = async () => {
    try {
      const response = await pingAPI.generateFirebaseIdToken();
      const formattedResponse = JSON.stringify(response.data, null, 2);

      const loginResponse = await userAPI.login(
        response.data.testFirebaseIdToken
      );
      setVxUserInfo(loginResponse.data);
    } catch (error) {
      console.log("generateUserAndLgoIn error", error);
    }
  };

  const callRequestFunds = async () => {
    let requestParams = {
      userId: vxUserInfo.id,
      amount: 1000,
      currency: "eur",
    };
    pingAPI
      .requestFunds(vxUserInfo.vxToken, requestParams)
      .then((response) => {
        console.log("Funds requested:", response.data);
        // Handle successful response
      })
      .catch((error) => {
        console.error("Error requesting funds:", error);
        // Handle error
      });
  };

  const callRequestRon = async () => {
    let requestParams = {
      userId: vxUserInfo.id,
      amount: 1000,
      currency: "ron",
    };
    pingAPI
      .requestFunds(vxUserInfo.vxToken, requestParams)
      .then((response) => {
        console.log("Funds requested:", response.data);
        // Handle successful response
      })
      .catch((error) => {
        console.error("Error requesting funds:", error);
        // Handle error
      });
  };

  const callInitiateVxGamingCurrency = async (currency) => {
    let initiateVxGamingParams = {
      currency: currency,
    };
    pingAPI
      .initiateVxGamingCurrency(vxUserInfo.vxToken, initiateVxGamingParams)
      .then((response) => {
        console.log("initiateVxGamingCurrency response:", response.data);
        // Handle successful response
        // Open the payUrl in a new window/tab
        window.open(response.data.payUrl, "_blank");
      })
      .catch((error) => {
        console.error("Error initiateVxGamingCurrency:", error);
        // Handle error
      });
  };

  return (
    <div className="p-4">
      <p className="mb-3 text-gray-500 dark:text-gray-400">
        This will work only if you are using localhost backend. It is useful to
        test the ui without having to deploy. It is very helpful for backend
        debuting.
      </p>

      <div className="grid grid-cols-4 gap-4 p-4">
        <div className="col-span-1">
          <p className="mb-4">Step 0: Generate user and login</p>
          <button
            onClick={generateUserAndLgoIn}
            className="bg-blue-500 text-white px-4 py-2 rounded"
          >
            Generate user and login
          </button>
        </div>

        <div className="col-span-3">
          <p>
            Use this only if you are using the localhost backend services. Call
            this if you are not logged or you want to login as a new random
            user.
          </p>
          <p>
            This is all you need to start basic frontend development with
            localhost support
          </p>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4 p-4">
        <div className="col-span-1">
          <p className="mb-4">decoded vxUserInfo from context</p>
        </div>

        <div className="col-span-3">
          <p>
            {vxUserInfo && (
              <pre className="p-4" style={{ whiteSpace: "pre-wrap" }}>
                {JSON.stringify(vxUserInfo, null, 2)}
              </pre>
            )}
          </p>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4 p-4">
        <div className="col-span-1">
          <p className="mb-4">Platform funds</p>
          <button
            onClick={() => callInitiateVxGamingCurrency("eur")}
            className="bg-blue-500 text-white px-4 py-2 rounded"
          >
            Initiate platform euro funds
          </button>
        </div>

        <div className="col-span-3">
          <p>It adds more euro to the platform.</p>
          <ul className="list-disc ml-4">
            <li>
              It only works in testing and you only need to call this once a day
            </li>
            <li>Use this card info after you get redirected</li>
            <li>Card: 4000000000000077</li>
          </ul>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4 p-4">
        <div className="col-span-1">
          <p className="mb-4">Platform funds</p>
          <button
            onClick={() => callInitiateVxGamingCurrency("ron")}
            className="bg-blue-500 text-white px-4 py-2 rounded"
          >
            Initiate platform ron funds
          </button>
        </div>

        <div className="col-span-3">
          <p>It adds more ron to the platform.</p>
          <ul className="list-disc ml-4">
            <li>
              It only works in testing and you only need to call this once a day
            </li>
            <li>Use this card info after you get redirected</li>
            <li>Card: 4000000000000077</li>
          </ul>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4 p-4">
        <div className="col-span-1">
          <p className="mb-4">Ad more funds</p>
          <button
            onClick={callRequestFunds}
            className="bg-blue-500 text-white px-4 py-2 rounded"
          >
            Request EUR
          </button>
        </div>

        <div className="col-span-3">
          <p>It adds more euro to the current user</p>
          <p>
            Is not that simple in the real world. This will fail in production
            and also in develop when the stars are not alined
          </p>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4 p-4">
        <div className="col-span-1">
          <p className="mb-4">Ad more funds</p>
          <button
            onClick={callRequestRon}
            className="bg-blue-500 text-white px-4 py-2 rounded"
          >
            Request RON
          </button>
        </div>

        <div className="col-span-3">
          <p>It adds more ron to the current user</p>
          <p>
            Is not that simple in the real world. This will fail in production
            and also in develop when the stars are not alined
          </p>
        </div>
      </div>

      <div className="grid grid-cols-4 gap-4 p-4">
        <div className="col-span-1">
          <p className="mb-4">Step 1: generate a random user</p>
          <button
            onClick={fetchGenerateFirebaseIdToken}
            className="bg-blue-500 text-white px-4 py-2 rounded"
          >
            Generate user
          </button>
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
          <p>
            {whoAmIFormattedResponse && (
              <pre className="p-4" style={{ whiteSpace: "pre-wrap" }}>
                {whoAmIFormattedResponse}
              </pre>
            )}
          </p>
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
    </div>
  );
}
