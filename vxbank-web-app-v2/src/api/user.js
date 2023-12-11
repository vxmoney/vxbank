import axios from "axios";
import configValues from "./apiConfig";

const { protocol, baseUrl, port } = configValues;

const instance = axios.create({
  baseURL: `${protocol}://${baseUrl}:${port}/user`,
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
};
