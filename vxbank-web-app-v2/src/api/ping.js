import axios from "axios";
import configValues from "./apiConfig";

const { protocol, baseUrl, port } = configValues;

const instance = axios.create({
  baseURL: `${protocol}://${baseUrl}:${port}/ping`,
  headers: {
    "Content-Type": "application/json",
  },
});

export const pingAPI = {
    getEnvironment: () => instance.get("/getEnvironment"),
}
