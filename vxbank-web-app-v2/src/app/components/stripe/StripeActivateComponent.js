import { useEffect, useState } from "react";
import configValues from "../../../api/apiConfig";
import { stripeConfigAPI } from "@/api/stripeConfig";
import { userAPI } from "@/api/user";
import { UserAuth } from "@/app/context/AuthContext";

/**
 * How it works
 * Check stripe config endpoint: stripeConfig/getByUserId/{userId}
 *
 * If response state != active
 * Initiate stripe config initiate will handel also resume.
 * Initiate config endpoint: stripeConfig/initiateConfig
 * use params
 * public class StripeConfigInitiateConfigParams {
 *     public Long userId;
 * }
 */

const StripeActivateComponent = ({ id, email, name }) => {
  const { frontendPort, frontendBaseUrl, frontendProtocol } = configValues;
  const { vxUserInfo, setVxUserInfo } = UserAuth();

  const [stripeConfigState, setStripeConfigState] = useState(null);
  const [configUrl, setConfigUrl] = useState(null);

  const callGetStripeConfig = async () => {
    try {
      const getStripeConfigResponse = await stripeConfigAPI.getByUserId(
        vxUserInfo.vxToken,
        vxUserInfo.id
      );
      setStripeConfigState(getStripeConfigResponse.data.state);
    } catch (error) {
      console.error("callGetStripeConfig error: ", error);
    }
  };

  useEffect(() => {
    callGetStripeConfig();
  }, []);

  const initiateResumeConfiguration = async () => {
    let requestParams = {
      userId: vxUserInfo.id,
    };
    try {
      console.log("clicked initiate resume configuration");
      //window.location.href = 'https://www.google.com'
      const initiateResponse = await stripeConfigAPI.initiateConfig(
        vxUserInfo.vxToken,
        requestParams
      );
      console.log("initiateResponse", initiateResponse);
      if (initiateResponse.data.configurationComplete) {
        console.log("Time to refresh token");
        const refreshResponse = await userAPI.refreshVxToken(
          vxUserInfo.vxToken
        );
        console.log("refreshResponse", refreshResponse.data);
        setVxUserInfo(refreshResponse.data);
      } else {
        let stripeUrl = initiateResponse.data.url;
        window.location.href = stripeUrl;
      }
    } catch (error) {
      console.error("initiateResumeConfiguration error", error);
    }
  };

  return (
    <div className="pl-8 pr-8">
      <div className="block  p-6 bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700 ">
        <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
          Stripe activate card
        </h5>
        <p className="font-normal text-gray-700 dark:text-gray-400k">
          You need to update stripe configuration
        </p>

        <div className="flex-grow overflow-auto">
          You will be redirected to vxbank stripe configuration page in order to
          update your configuration
        </div>
        <table className="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400">
          <tbody>
            <tr className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
              <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                Stripe config state
              </th>
              <td
                className="px-6 py-4"
                style={{ display: "flex", alignItems: "center" }}
              >
                <p className="pr-2">{stripeConfigState} </p>

                <button
                  type="button"
                  class="py-2.5 px-5 me-2 mb-2 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-200 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
                  onClick={initiateResumeConfiguration}
                >
                  Initiate / Resume configuration
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
};
export default StripeActivateComponent;
