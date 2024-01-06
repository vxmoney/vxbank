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
  getByUserId: (vxToken, userId) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    const endpoint = `/getByUserId/${userId}`;
    return instance.get(endpoint, { headers });
  },
  initiateConfig: (vxToken, initiateConfigParams) =>{
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.post("/initiateConfig", initiateConfigParams, { headers });
  }
};
