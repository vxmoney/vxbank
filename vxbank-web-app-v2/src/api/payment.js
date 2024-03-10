import axios from "axios";
import configValues from "./apiConfig";

const { protocol, baseUrl, port } = configValues;

const baseURL = `${protocol}://${baseUrl}${protocol === 'https' ? '' : `:${port}`}/payment`;

const instance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const paymentAPI = {
  depositFiat: (vxToken, depositFiatParams) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.post("/depositFiat", depositFiatParams, { headers });
  },
};
