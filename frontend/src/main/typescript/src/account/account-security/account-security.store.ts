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
  }),
  getters: {},
  actions: {
    init() {
      this.otpEnabled = applicationStore.user?.otpEnabled ?? false;
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
          this.computeActionDeleteAccount = true;
          userApi
            .deleteUserMe()
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

    setupOtp() {
      this.computeActionSetupOtp = true;
      userApi
        .setupOtp()
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
        .confirmOtp({ otp: this.otpCode })
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
          if (error.response?.data?.message === "Invalid OTP") {
            applicationStore.sendNotification(
              "danger",
              "notification.error.invalid-otp"
            );
          } else {
            applicationStore.axiosException(error);
          }
        })
        .finally(() => {
          this.computeActionConfirmOtp = false;
        });
    },

    disableOtp() {
      this.computeActionDisableOtp = true;
      userApi
        .disableOtp()
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
