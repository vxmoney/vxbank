import axios from "axios";
import configValues from "./apiConfig";

const { protocol, baseUrl, port } = configValues;
const baseURL = `${protocol}://${baseUrl}${
  protocol === "https" ? "" : `:${port}`
}/eventparticipant`;

const instance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const eventParticipantAPI = {
    getByEventId: (
    vxToken,
    eventId
  ) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.get(`getByEventId/${eventId}`, { headers });
  },
};
