import { useEffect, useState } from "react";
import configValues from "../../../api/apiConfig";
import { stripeConfigAPI } from "@/api/stripeConfig";
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
  const { vxUserInfo } = UserAuth();

  const [stripeConfigState, setStripeConfigState] = useState(null);

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
      </div>
    </div>
  );
};
export default StripeActivateComponent;
