import { defineStore } from "pinia";
import type { Notification, NotificationType } from "@/core/model/notification";
import {sessionApi, userApi, setAccessToken} from "@/core/util/api-util";
import type {UserDto} from "api-generated";

const NOTIFICATION_DURATION = 3000;
const MAX_NOTIFICATIONS = 5;

const EMAIL_LOCAL_STORAGE_KEY = "email";
const REFRESH_TOKEN_LOCAL_STORAGE_KEY = "refreshToken";

export const useApplicationStore = defineStore("application", {
  state: () => ({
    user: null as UserDto | null,
    initOk: false,
    refreshToken: "",
    accessToken: "",
    notifications: [] as Notification[],
    lastNotificationId: 0,
    modal: {
      show: false,
      title: "",
      content: "",
      resolveMessage: "validate",
      rejectMessage: "",
      resolve: (value: unknown) => {},
      reject: () => {},
    },
  }),
  getters: {
    isAuthenticated: (state) => (state.user !== null && state.refreshToken !== "") ||
      (!!localStorage.getItem(EMAIL_LOCAL_STORAGE_KEY) && !!localStorage.getItem(REFRESH_TOKEN_LOCAL_STORAGE_KEY)),
  },
  actions: {
    async init() {
      const email = localStorage.getItem(EMAIL_LOCAL_STORAGE_KEY) || "";
      this.refreshToken = localStorage.getItem(REFRESH_TOKEN_LOCAL_STORAGE_KEY) || "";
      await this.renew(email);
      await this.login(email, this.accessToken, this.refreshToken);
      this.initOk = true;
    },

    sendNotification(type: NotificationType, message: string) {
      const notificationId = this.lastNotificationId++;

      if (this.notifications.length >= MAX_NOTIFICATIONS) {
        this.notifications.splice(0, 1);
      }

      this.notifications.push({
        id: notificationId,
        message: this.$i18n.t(message),
        type,
      });

      setTimeout(() => this.closeNotification(notificationId), NOTIFICATION_DURATION);
    },

    closeNotification(id: number) {
      for(let notificationIndex in this.notifications) {
        if (this.notifications[notificationIndex].id === id) {
          this.notifications.splice(+notificationIndex, 1);
          break;
        }
      }
    },

    showModal(title: string, content: string, resolveMessage = "validate", rejectMessage = "") {
      this.modal.show = true;
      this.modal.title = title;
      this.modal.content = content;
      this.modal.resolveMessage = resolveMessage;
      this.modal.rejectMessage = rejectMessage;

      return new Promise((resolve, reject) => {
        this.modal.resolve = resolve;
        this.modal.reject = reject;
      });
    },

    validateModal() {
      this.modal.show = false;
      this.modal.resolve(null);
    },

    closeModal() {
      this.modal.show = false;
      this.modal.reject();
    },

    axiosException(exception: any) {
      if (exception.isAxiosError) {
        if (exception.response.status === 401 || exception.response.status === 403) {
          this.disconnected();
        } else {
          this.sendNotification("alert", exception.response.statusText);
        }
      }
    },

    async login(email: string, accessToken: string, refreshToken?: string) {
      this.accessToken = accessToken;
      setAccessToken(accessToken);
      if (refreshToken) {
        await userApi.getUserMe().then((response) => {
          this.user  = response.data;
          this.refreshToken = refreshToken;
          localStorage.setItem(EMAIL_LOCAL_STORAGE_KEY, email);
          localStorage.setItem(REFRESH_TOKEN_LOCAL_STORAGE_KEY, refreshToken);
        }).catch(this.axiosException)
      }
    },

    disconnected() {
      localStorage.clear();
      this.user = null;
      this.accessToken = "";
      this.refreshToken = "";
      this.$router.push({ name: "home" });
      setAccessToken();
    },

    logout() {
      this.disconnected();
      sessionApi.logout().then(() => {}).catch(this.axiosException);
    },

    async renew(email: string) {
      if (email && this.refreshToken) {
        await sessionApi.renewSession({
          email: email,
          refreshToken: this.refreshToken,
        }).then((response) => {
          const { accessToken, refreshToken } = response.data
          this.login(email, accessToken, refreshToken);
        }).catch((exception) => {
          this.axiosException(exception);
          this.disconnected();
        });
      }
    },
  },
});
