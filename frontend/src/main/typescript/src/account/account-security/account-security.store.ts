import { defineStore } from "pinia";
import { sessionApi, userApi } from "@/core/util/api-util";
import { useApplicationStore } from "@/core/application.store";
import type { RefreshTokenPropertiesDto } from "@generated/api";

const applicationStore = useApplicationStore();

export const useAccountSecurityStore = defineStore("account-security", {
  state: () => ({
    sessions: [] as RefreshTokenPropertiesDto[],
  }),
  getters: {},
  actions: {
    init() {
      this.loadSessions();
    },

    loadSessions() {
      sessionApi
        .findSessions()
        .then((response) => {
          this.sessions = response.data;
        })
        .catch(applicationStore.axiosException);
    },

    async deleteAccount() {
      applicationStore
        .showModal(
          "modal.are-you-sure",
          "modal.account-deletion-validation",
          "modal.yes",
          "modal.no"
        )
        .then(() => {
          userApi
            .deleteUserMe()
            .then(() => {
              applicationStore.sendNotification(
                "info",
                "notification.account-deleted"
              );
              applicationStore.disconnected();
            })
            .catch(applicationStore.axiosException);
        })
        .catch();
    },

    deleteSession(sessionUuid: string) {
      sessionApi
        .deleteSession(sessionUuid)
        .then(() => {
          this.loadSessions();
        })
        .catch(applicationStore.axiosException);
    },
  },
});
