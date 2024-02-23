import axios from "axios";
import configValues from "./apiConfig";

const { protocol, baseUrl, port } = configValues;
const baseURL = `${protocol}://${baseUrl}${
  protocol === "https" ? "" : `:${port}`
}/eventresult`;

const instance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const eventResultsAPI = {
  create: (vxToken, eventResultCreateParams) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    const params = eventResultCreateParams;
    return instance.post("", params, { headers });
  },
  getByEventId: (vxToken, eventId) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };

    return instance.get(`/getByEventId/${eventId}`, { headers });
  },
};
