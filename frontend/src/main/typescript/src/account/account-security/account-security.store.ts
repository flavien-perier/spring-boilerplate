import { defineStore } from "pinia";
import { sessionApi, userApi } from "@/core/util/api-util";
import { useApplicationStore } from "@/core/application.store";
import type { RefreshTokenPropertiesDto } from "@generated/api";

const applicationStore = useApplicationStore();

export const useAccountSecurityStore = defineStore("account-security", {
  state: () => ({
    sessions: [] as RefreshTokenPropertiesDto[],
    otpEnabled: false,
    otpSetupUri: null as string | null,
    otpCode: "",
    computeActionSetupOtp: false,
    computeActionConfirmOtp: false,
    computeActionDisableOtp: false,
    computeActionDeleteAccount: false,
    currentPage: 1,
    pageSize: 10,
    totalElements: 0,
    totalPages: 1,
  }),
  getters: {},
  actions: {
    init() {
      this.otpEnabled = applicationStore.user?.otpEnabled ?? false;
      this.loadSessions();
    },

    loadSessions() {
      sessionApi
        .findSessions(this.currentPage, this.pageSize)
        .then((response) => {
          this.sessions = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
        })
        .catch(applicationStore.axiosException);
    },
    setPage(page: number) {
      this.currentPage = page;
      this.loadSessions();
    },
    setPageSize(size: number) {
      this.pageSize = size;
      this.currentPage = 1;
      this.loadSessions();
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
          this.computeActionDeleteAccount = true;
          userApi
            .deleteCurrentUser()
            .then(() => {
              applicationStore.sendNotification(
                "info",
                "notification.account-deleted"
              );
              applicationStore.disconnected();
            })
            .catch(applicationStore.axiosException)
            .finally(() => {
              this.computeActionDeleteAccount = false;
            });
        })
        .catch(() => {});
    },

    deleteSession(sessionUuid: string) {
      applicationStore
        .showModal(
          "modal.are-you-sure",
          "modal.session-deletion-validation",
          "modal.yes",
          "modal.no"
        )
        .then(() => {
          sessionApi
            .deleteSession(sessionUuid)
            .then(() => {
              applicationStore.sendNotification(
                "info",
                "notification.session-deleted"
              );
              this.loadSessions();
            })
            .catch(applicationStore.axiosException);
        })
        .catch(() => {});
    },

    setupOtp() {
      this.computeActionSetupOtp = true;
      userApi
        .setupCurrentUserOtp()
        .then((response) => {
          this.otpSetupUri = response.data.uri;
        })
        .catch(applicationStore.axiosException)
        .finally(() => {
          this.computeActionSetupOtp = false;
        });
    },

    async confirmOtp() {
      this.computeActionConfirmOtp = true;
      await userApi
        .confirmCurrentUserOtp({ otp: this.otpCode })
        .then(() => {
          this.otpSetupUri = null;
          this.otpEnabled = true;
          this.otpCode = "";
          applicationStore.sendNotification(
            "info",
            "notification.otp-confirmed"
          );
          return applicationStore.login(applicationStore.accessToken);
        })
        .catch((error: any) => {
          this.otpCode = "";
          applicationStore.axiosException(error);
        })
        .finally(() => {
          this.computeActionConfirmOtp = false;
        });
    },

    disableOtp() {
      this.computeActionDisableOtp = true;
      userApi
        .disableCurrentUserOtp()
        .then(() => {
          this.otpEnabled = false;
          applicationStore.sendNotification(
            "info",
            "notification.otp-disabled"
          );
        })
        .catch(applicationStore.axiosException)
        .finally(() => {
          this.computeActionDisableOtp = false;
        });
    },
  },
});
