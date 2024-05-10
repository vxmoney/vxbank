import axios from "axios";
import configValues from "./apiConfig";

const { protocol, baseUrl, port } = configValues;
const baseURL = `${protocol}://${baseUrl}${
  protocol === "https" ? "" : `:${port}`
}/publicEventSellingPoint`;

const instance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const publicEventSellingPointAPI = {
  getAll: (vxToken, publicEventId) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.get(``, { headers, params: { publicEventId } });
  },

  create: (vxToken, body) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };

    return instance.post(``, body, { headers });
  },
};
