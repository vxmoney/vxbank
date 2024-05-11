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

  update: (vxToken, pointId, body) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    // Ensure pointId is a number and included in the URL path
    if (typeof pointId !== 'number') {
      throw new Error('pointId must be a number');
    }
    return instance.put(`/${pointId}`, body, { headers });
  },

  get: (vxToken, pointId) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    // Ensure pointId is a number and included in the URL path
    if (typeof pointId !== 'number') {
      throw new Error('pointId must be a number');
    }
    return instance.get(`/${pointId}`, { headers });
  }
};
