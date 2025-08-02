import { defineStore } from "pinia";
import {useApplicationStore} from "@/core/application.store";
import {userApi} from "@/core/util/api-util";

const applicationStore = useApplicationStore();

export const useForgotPasswordStore = defineStore("forgot-password", {
  state: () => ({
    email: "",
    computeAction: false,
  }),
  getters: {
    buttonEnabled: (state) => state.email !== "" && !state.computeAction
  },
  actions: {
    init() {

    },

    close() {
      this.$reset();
    },

    send() {
      if (!this.buttonEnabled) {
        return;
      }
      this.computeAction = true;

      userApi.forgotPassword(this.email).then(() => {
        applicationStore.sendNotification("info", "email-sent");
        this.$router.push({ name: "login" });
      }).catch(applicationStore.axiosException).finally(() => { this.computeAction = false });
    },
  },
});
