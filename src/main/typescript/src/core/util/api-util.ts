import {Configuration, SessionApiFactory, UserApiFactory} from "api-generated";
import {useApplicationStore} from "@/core/application.store";
import axios from "axios";
import axiosRetry from "axios-retry";

const BASE_PATH = window.location.origin;

const axiosInstance = axios.create({
  baseURL: BASE_PATH,
  timeout: 2000,
  withCredentials: true,
  headers: {
    Accept: "application/json",
    "Content-Type": "application/json",
  },
});

const configuration = new Configuration();
const userApi = UserApiFactory(configuration, BASE_PATH, axiosInstance);
const sessionApi = SessionApiFactory(configuration, BASE_PATH, axiosInstance);

function setAccessToken(accessToken?: string) {
  if (accessToken) {
    axiosInstance.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
  } else {
    axiosInstance.defaults.headers.common["Authorization"] = undefined;
  }
}

axiosRetry(axiosInstance, {
  retries: 1,
  retryCondition: (error) => {
    const isSession = error.response?.config?.url === "/api/session" && error.response?.config?.method == "post";
    const status = error?.response?.status || 0;
    return (status === 401 || status === 403) && !isSession && useApplicationStore().isAuthenticated;
  },
  retryDelay: (retryCount) => {
    return 500 * retryCount;
  },
  onRetry: async (retryCount, error, requestConfig) => {
    const applicationStore = useApplicationStore();

    if (retryCount === 1 && applicationStore.isAuthenticated) {
      await applicationStore.renew(applicationStore!.user!.email);
    } else {
      applicationStore.disconnected();
    }
  },
});

export { userApi, sessionApi, setAccessToken };
