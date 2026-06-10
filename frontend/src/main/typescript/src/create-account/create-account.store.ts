import { defineStore } from "pinia";
import { useApplicationStore } from "@/core/application.store";
import { userApi } from "@/core/util/api-util";
import { passwordUtil } from "@generated/component-library";

const applicationStore = useApplicationStore();

export const useCreateAccountStore = defineStore("create-account", {
  state: () => ({
    email: "",
    password: "",
    isEmailValid: false,
    isPasswordValid: false,
    computeAction: false,
  }),
  getters: {
    buttonEnabled: (state) =>
      state.isEmailValid && state.isPasswordValid && !state.computeAction,
  },
  actions: {
    init() {},

    close() {
      this.$reset();
    },

    async createAccount() {
      if (!this.buttonEnabled) {
        return;
      }
      this.computeAction = true;

      userApi
        .createUser({
          email: this.email,
          password: this.password,
          proofOfWork: await passwordUtil.proofOfWork(
            this.password,
            this.email
          ),
        })
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.account-created"
          );
          this.$router.push({ name: "login" });
        })
        .catch((error: any) => {
          if (error.response?.status === 409) {
            applicationStore.sendNotification(
              "danger",
              "notification.error.email-already-used"
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
