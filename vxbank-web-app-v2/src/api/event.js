import axios from "axios";
import configValues from "./apiConfig";

const { protocol, baseUrl, port } = configValues;
const baseURL = `${protocol}://${baseUrl}${
  protocol === "https" ? "" : `:${port}`
}/event`;

const instance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const eventAPI = {
  create: (vxToken, eventData) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.post("", eventData, { headers });
  },
  search: (
    vxToken,
    vxIntegrationId = "vxGaming",
    vxGame = "leagueOfLegends",
    stateList = ["openForRegistration", "inProgress", "closed"],
    offset = 0,
    limit = 20
  ) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    const params = {
      vxIntegrationId,
      vxGame,
      stateList: stateList.join(","),
      offset,
      limit,
    };
    return instance.get("", { headers, params });
  },
  getById: (vxToken, eventId) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };

    return instance.get(`/${eventId}`, { headers });
  },
  join: (vxToken, eventData) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.post("/join", eventData, { headers });
  },
  payJoin: (vxToken, eventData) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.post("/payJoin", eventData, { headers });
  },
  closeEvent: (vxToken, closeEventParams) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.post(`/closeEvent`, closeEventParams, { headers });
  },
  payCreate: (vxToken, eventData) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.post("payCreate", eventData, { headers });
  },
};
