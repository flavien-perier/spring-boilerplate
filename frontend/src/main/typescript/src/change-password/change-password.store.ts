import { defineStore } from "pinia";
import { useApplicationStore } from "@/core/application.store";
import { userApi } from "@/core/util/api-util";
import { passwordUtil } from "@generated/component-library";

const applicationStore = useApplicationStore();

export const useChangePasswordStore = defineStore("change-password", {
  state: () => ({
    email: "",
    token: "",
    password: "",
    isPasswordValid: false,
    computeAction: false,
  }),
  getters: {
    buttonEnabled: (state) => state.isPasswordValid && !state.computeAction,
  },
  actions: {
    init(email?: string, token?: string) {
      this.email = email || "";
      this.token = token || "";
    },

    close() {
      this.$reset();
    },

    async update() {
      if (!this.buttonEnabled) {
        return;
      }
      this.computeAction = true;

      userApi
        .updatePassword({
          token: this.token,
          password: this.password,
          proofOfWork: await passwordUtil.proofOfWork(this.password, this.email),
        })
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.password-updated"
          );
          this.$router.push({ name: "login" });
        })
        .catch(applicationStore.axiosException)
        .finally(() => {
          this.computeAction = false;
        });
    },
  },
});
