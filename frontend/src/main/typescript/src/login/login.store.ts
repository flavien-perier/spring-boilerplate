import { defineStore } from "pinia";
import { useApplicationStore } from "@/core/application.store";
import {sessionApi, userApi, getErrorCode} from "@/core/util/api-util";
import { passwordUtil } from "@generated/component-library";

const applicationStore = useApplicationStore();

export const useLoginStore = defineStore("login", {
  state: () => ({
    email: "",
    password: "",
    computeAction: false,
    isEmailValid: false,
    otp: "",
    otpRequired: false,
  }),
  getters: {
    buttonEnabled: (state) =>
      state.email !== "" &&
      state.isEmailValid &&
      state.password !== "" &&
      !state.computeAction &&
      (!state.otpRequired || state.otp.length === 6),
  },
  actions: {
    init(activationToken?: string) {
      if (activationToken) {
        userApi
          .activateUser(activationToken)
          .then(() => {
            applicationStore.sendNotification(
              "info",
              "notification.account-activated"
            );
          })
          .catch((error: any) => {
            if (error.response && error.response.status < 500) {
              applicationStore.sendNotification(
                "danger",
                "notification.account-activation-failed"
              );
            } else {
              applicationStore.axiosException(error);
            }
          });
      }
    },

    close() {
      this.$reset();
    },

    async login() {
      if (!this.buttonEnabled) {
        return;
      }
      this.computeAction = true;

      sessionApi
        .loginWeb({
          email: this.email,
          password: this.password,
          proofOfWork: await passwordUtil.proofOfWork(
            this.password,
            this.email
          ),
          otp: this.otpRequired ? this.otp : undefined,
        })
        .then((response) => {
          const { accessToken } = response.data;
          applicationStore.login(accessToken);
          this.$router.push({ name: "home" });
        })
        .catch((error: any) => {
          if (!error.response) {
            applicationStore.axiosException(error);
            return;
          }
          const errorCode: string = getErrorCode(error.response?.data);
          if (errorCode === "OTP_REQUIRED") {
            this.otpRequired = true;
          } else if (errorCode === "INVALID_OTP") {
            this.otp = "";
            applicationStore.sendNotification(
              "danger",
              "notification.error.invalid-otp"
            );
          } else if (error.response.status === 401) {
            this.password = "";
            applicationStore.sendNotification(
              "danger",
              "notification.authentication-failed"
            );
          } else {
            applicationStore.axiosException(error);
          }
        })
        .finally(() => {
          this.computeAction = false;
        });
    },
  },
});
