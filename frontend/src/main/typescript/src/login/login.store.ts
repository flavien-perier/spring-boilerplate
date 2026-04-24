import { defineStore } from "pinia";
import { useApplicationStore } from "@/core/application.store";
import { sessionApi, userApi } from "@/core/util/api-util";
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
          .catch(() => {
            applicationStore.sendNotification(
              "danger",
              "notification.account-activation-failed"
            );
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
          proofOfWork: await passwordUtil.proofOfWork(this.password, this.email),
          otp: this.otpRequired ? this.otp : undefined,
        })
        .then((response) => {
          const { accessToken } = response.data;
          applicationStore.login(accessToken);
          this.$router.push({ name: "home" });
        })
        .catch((error: any) => {
          if (error.response?.data?.detail === "OTP required") {
            this.otpRequired = true;
          } else if (
            error.response?.data?.detail === "Invalid OTP" ||
            this.otpRequired
          ) {
            this.otp = "";
            applicationStore.sendNotification(
              "danger",
              "notification.error.invalid-otp"
            );
          } else {
            this.password = "";
            applicationStore.sendNotification(
              "danger",
              "notification.authentication-failed"
            );
          }
        })
        .finally(() => {
          this.computeAction = false;
        });
    },
  },
});
