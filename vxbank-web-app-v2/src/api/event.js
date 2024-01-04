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
  whoAmI: (vxToken) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.get("/whoAmI", { headers });
  },
  create: (vxToken, eventData) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.post("", eventData, { headers });
  },
};
