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
  search: (vxToken, vxUserId) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };

    return instance.get("", { headers, params: { vxUserId } });
  },
  getById: (vxToken, eventId) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };

    return instance.get(`/${eventId}`, { headers });
  },
  join: (vxToken, eventId) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.get(`/${eventId}/checkRegisterClient`, { headers });
  },
  clientDepositFunds: (vxToken, eventId, value) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.post(
      `/${eventId}/clientDepositFunds`,
      { value },
      { headers }
    );
  },
};
