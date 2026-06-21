import { defineStore } from "pinia";
import type { Notification, NotificationType } from "@/core/model/notification";
import {
  applicationApi,
  sessionApi,
  userApi,
  setAccessToken,
  getErrorCode,
} from "@/core/util/api-util";
import type { UserDto } from "@generated/api";
import { cookieUtil } from "@/core/util/cookie-util";

const NOTIFICATION_DURATION = 3000;
const MAX_NOTIFICATIONS = 5;

const EMAIL_LOCAL_STORAGE_KEY = "email";
const THEME_LOCAL_STORAGE_KEY = "fio-theme";

export const useApplicationStore = defineStore("application", {
  state: () => ({
    user: null as UserDto | null,
    configuration: {
      minPasswordLength: 1,
    },
    initOk: false,
    accessToken: "",
    notifications: [] as Notification[],
    lastNotificationId: 0,
    theme: (localStorage.getItem(THEME_LOCAL_STORAGE_KEY) ?? "light") as
      | "light"
      | "dark",
    modal: {
      show: false,
      title: "",
      content: "",
      resolveMessage: "action.validate",
      rejectMessage: "",
      resolve: (value: unknown) => {},
      reject: () => {},
    },
  }),
  getters: {
    isAuthenticated: (state) => state.user !== null && state.accessToken !== "",
  },
  actions: {
    toggleTheme() {
      this.theme = this.theme === "light" ? "dark" : "light";
      localStorage.setItem(THEME_LOCAL_STORAGE_KEY, this.theme);
      document.documentElement.setAttribute("data-theme", this.theme);
    },

    async init() {
      document.documentElement.setAttribute("data-theme", this.theme);
      const email = cookieUtil.get(EMAIL_LOCAL_STORAGE_KEY) || "";

      await applicationApi
        .getConf()
        .then((response) => {
          this.configuration = response.data;
        })
        .catch(() => {
          this.sendNotification(
            "danger",
            "http.error.503"
          );
        });

      await this.renew(email);
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

      setTimeout(
        () => this.closeNotification(notificationId),
        NOTIFICATION_DURATION
      );
    },

    closeNotification(id: number) {
      const index = this.notifications.findIndex((n) => n.id === id);
      if (index !== -1) {
        this.notifications.splice(index, 1);
      }
    },

    showModal(
      title: string,
      content: string,
      resolveMessage = "validate",
      rejectMessage = ""
    ) {
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
      if (!exception.isAxiosError) {
        return;
      }

      if (!exception.response) {
        this.sendNotification("danger", "http.error.network");
        return;
      }

      const status: number = exception.response.status;
      const errorCode: string = getErrorCode(exception.response.data);

      if (errorCode && this.$i18n.t(`http.error.${errorCode}`) !== `http.error.${errorCode}`) {
        this.sendNotification("danger", `http.error.${errorCode}`);
        return;
      }

      if (status === 401) {
        this.disconnected();
        return;
      }

      const statusKey = `http.error.${status}`;
      if (this.$i18n.t(statusKey) !== statusKey) {
        this.sendNotification("danger", statusKey);
        return;
      }

      if (status >= 500) {
        this.sendNotification("danger", "http.error.500");
        return;
      }

      this.sendNotification("danger", "http.error.unknown");
    },

    async login(accessToken: string = "") {
      setAccessToken(accessToken);

      await userApi
        .getCurrentUser()
        .then((response) => {
          this.user = response.data;
          this.accessToken = accessToken;
        })
        .catch(this.axiosException);
    },

    disconnected() {
      cookieUtil.clearAll();
      this.user = null;
      this.accessToken = "";
      this.$router.push({ name: "home" });
      setAccessToken();
    },

    logout() {
      sessionApi.logout().catch(this.axiosException).finally(this.disconnected);
    },

    async renew(email: string) {
      if (!email || email === "") {
        return;
      }

      await sessionApi
        .renewSessionWeb("default", {
          email: email,
        })
        .then((response) => {
          this.login(response.data.accessToken);
        })
        .catch((exception) => {
          this.axiosException(exception);
        });
    },
  },
});
