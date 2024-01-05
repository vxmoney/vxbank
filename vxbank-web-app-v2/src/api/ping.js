import axios from "axios";
import configValues from "./apiConfig";

const { protocol, baseUrl, port } = configValues;

const baseURL = `${protocol}://${baseUrl}${protocol === 'https' ? '' : `:${port}`}/ping`;

const instance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const pingAPI = {
  getEnvironment: () => instance.get("/getEnvironment"),
  generateFirebaseIdToken: () => instance.get("/generateFirebaseIdToken"),
  whoAmI: (vxToken) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.get("/whoAmI", { headers });
  },
  requestFunds: (vxToken, requestFundsParams) =>{
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.post("/requestFunds", requestFundsParams, { headers });
  }
};
