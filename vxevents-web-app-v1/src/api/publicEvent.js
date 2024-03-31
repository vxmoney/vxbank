import axios from "axios";
import configValues from "./apiConfig";

const { protocol, baseUrl, port } = configValues;
const baseURL = `${protocol}://${baseUrl}${
  protocol === "https" ? "" : `:${port}`
}/publicEvent`;

const instance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const publicEventAPI = {
  create: (vxToken, eventData) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.post("", eventData, { headers });
  },
  search: (
    vxToken,
    vxUserId
  ) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    
    return instance.get("", { headers, params: { vxUserId } });
  },
};
