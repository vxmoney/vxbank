import axios from "axios";
import configValues from "./apiConfig";

const { protocol, baseUrl, port } = configValues;
const baseURL = `${protocol}://${baseUrl}${protocol === 'https' ? '' : `:${port}`}/user`;

const instance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const userAPI = {
  login: (firebaseIdToken) => {
    const loginParams = {
      firebaseIdToken: firebaseIdToken,
    };
    return instance.post("/login", loginParams);
  },
  refreshVxToken: (vxToken) => {
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.get("/refreshVxToken", { headers });
  },
  getStripeLoginLink:(vxToken) =>{
    const headers = {
      Authorization: `Bearer ${vxToken}`,
    };
    return instance.get("/getStripeLoginLink", { headers });
  }
};
