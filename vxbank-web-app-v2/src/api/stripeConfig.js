import axios from "axios";
import configValues from "./apiConfig";

const { protocol, baseUrl, port } = configValues;
const baseURL = `${protocol}://${baseUrl}${
  protocol === "https" ? "" : `:${port}`
}/stripeConfig`;

const instance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const stripeConfigAPI = {
  create: (vxToken, eventData) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.post("", eventData, { headers });
  },
  search: (
    vxToken,
    vxIntegrationId = "vxGaming",
    stateList = ["openForRegistration", "inProgress", "closed"],
    offset = 0,
    limit = 5
  ) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    const params = {
      vxIntegrationId,
      stateList: stateList.join(","),
      offset,
      limit,
    };
    return instance.get("", { headers, params });
  },
  getByUserId: (vxToken, userId) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };

    const endpoint = `/getByUserId/${userId}`;
    console.log("getByUserId endpoint", endpoint)
    console.log("getByUserId vxToken", vxToken)

    return instance.get(endpoint, { headers });
  },
};
