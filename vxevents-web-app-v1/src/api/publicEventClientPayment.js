import axios from "axios";
import configValues from "./apiConfig";

const { protocol, baseUrl, port } = configValues;
const baseURL = `${protocol}://${baseUrl}${
  protocol === "https" ? "" : `:${port}`
}/publicEventClientPayment`;

const instance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const publicEventClientPaymentAPI = {
  getClientReport: (vxToken, eventId, clientId) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };

    return instance.get(
      `/getClientReport/event/${eventId}/client/${clientId}`,
      { headers }
    );
  },
};
