import { defineStore } from "pinia";
import {useApplicationStore} from "@/core/application.store";
import {sessionApi, userApi} from "@/core/util/api-util";
import {passwordUtil} from "@/core/util/password-util";

const applicationStore = useApplicationStore();

export const useLoginStore = defineStore("login", {
  state: () => ({
    email: "",
    password: "",
    computeAction: false,
  }),
  getters: {
    buttonEnabled: (state) => state.email !== "" && state.password !== "" && !state.computeAction
  },
  actions: {
    init(activationToken?: string) {
      if (activationToken) {
        userApi.activateUser(activationToken).then(() => {
          applicationStore.sendNotification("info", "account-activated");
        }).catch(() => {
          applicationStore.sendNotification("alert", "account-activation-failed");
        });
      }
    },

    close() {
      this.$reset();
    },

    login() {
      if (!this.buttonEnabled) {
        return;
      }
      this.computeAction = true;

      sessionApi.loginWeb({
        email: this.email,
        password: this.password,
        proofOfWork: passwordUtil.proofOfWork(this.password, this.email),
      }).then((response) => {
        const { accessToken } = response.data;
        applicationStore.login(accessToken);
        this.$router.push({ name: "home" });
      }).catch(() => {
        this.password = "";
        applicationStore.sendNotification("alert", "authentication-failed");
      }).finally(() => { this.computeAction = false });
    },
  },
});
