import { useEffect, useState } from "react";
import configValues from "../../../api/apiConfig";
import { stripeConfigAPI } from "@/api/stripeConfig";
import { userAPI } from "@/api/user";
import { UserAuth } from "@/app/context/AuthContext";
import { useSearchParams } from "next/navigation";

/**
 * How it works
 * Check stripe config endpoint: stripeConfig/getByUserId/{userId}
 */

const StripeActivateDifferentCurrenciesComponent = ({ id, email, name }) => {
  const searchParams = useSearchParams();
  const configStatus = searchParams.get("configStatus");
  const { frontendPort, frontendBaseUrl, frontendProtocol } = configValues;
  const { vxUserInfo, setVxUserInfo } = UserAuth();

  const callGetStripeLoginLink = async () => {
    try {
      const getStripeLoginLinkResponse = await userAPI.getStripeLoginLink(
        vxUserInfo.vxToken
      );
      console.log(
        "getStripeLoginLinkResponse",
        getStripeLoginLinkResponse.data
      );
      // Open the URI in a new tab or window
      if (getStripeLoginLinkResponse.data.uri) {
        window.open(getStripeLoginLinkResponse.data.uri, "_blank");
      }
    } catch (error) {
      console.error("refreshVxToken error: ", error);
    }
  };

  return (
    <div className="pl-8 pr-8 pt-8">
      <div className="block  p-6 bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700 ">
        <h5 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
          Stripe activate different currencies
        </h5>
        <p className="font-normal text-gray-700 dark:text-gray-400k">
          Use this card to activate different currencies
        </p>

        <div className="flex-grow overflow-auto">
          You will open your vxbank configuration page. You can activate
          different currencies only using this tool. After you are done
          validating your new currency account you wil have to refresh this page
          in order to confirm that the currency is active
        </div>
        <table className="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400">
          <tbody>
            <tr className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
              <th className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                vxbank dashboard
              </th>
              <td className="px-6 py-4">
                <div
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    gap: "8px",
                  }}
                >
                  <p>
                    This is what you need to do after you open the vxbank
                    dashboard
                  </p>
                  <ul className="list-disc pl-5">
                    <li>
                      Open your profile logo (top right side of the screen)
                    </li>
                    <li>Payout details / vxbank</li>
                    <li>Add an account</li>
                  </ul>
                  <button
                    type="button"
                    className="mt-auto py-2.5 px-5 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-200 dark:focus:ring-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:border-gray-600 dark:hover:text-white dark:hover:bg-gray-700"
                    onClick={callGetStripeLoginLink}
                  >
                    Open vxbank dashboard
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
};
export default StripeActivateDifferentCurrenciesComponent;
