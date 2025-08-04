import { defineStore } from "pinia";
import type { Notification, NotificationType } from "@/core/model/notification";
import {applicationApi, sessionApi, userApi, setAccessToken} from "@/core/util/api-util";
import type {UserDto} from "api-generated";
import {cookieUtil} from "@/core/util/cookie-util.ts";

const NOTIFICATION_DURATION = 3000;
const MAX_NOTIFICATIONS = 5;

const EMAIL_LOCAL_STORAGE_KEY = "email";

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
    isAuthenticated: (state) => state.user !== null && state.accessToken !== "",
  },
  actions: {
    async init() {
      const email = cookieUtil.get(EMAIL_LOCAL_STORAGE_KEY) || "";

      await applicationApi.getConf().then(response => {
        this.configuration = response.data;
      })

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

    async login(accessToken: string = "") {
      setAccessToken(accessToken);

      await userApi.getUserMe().then(response => {
        this.user  = response.data;
        this.accessToken = accessToken;
      }).catch(this.axiosException)
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

      await sessionApi.renewSessionWeb("default", {
        email: email,
      }).then(response => {
        this.login(response.data.accessToken);
      }).catch((exception) => {
        this.axiosException(exception);
        this.disconnected();
      });
    }
  },
});
