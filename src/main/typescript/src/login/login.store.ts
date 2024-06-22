import { defineStore } from "pinia";
import {useApplicationStore} from "@/core/application.store";
import {sessionApi, userApi} from "@/core/util/api-util";
import {passwordUtil} from "@/core/util/password-util";

const applicationStore = useApplicationStore();

export const useLoginStore = defineStore("login", {
  state: () => ({
    email: "",
    password: "",
  }),
  getters: {},
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
      sessionApi.login({
        email: this.email,
        password: passwordUtil.encode(this.password, this.email),
      }).then((response) => {
        const { refreshToken, accessToken } = response.data;
        applicationStore.login(this.email, accessToken, refreshToken);
        this.$router.push({ name: "home" });
      }).catch(() => {
        this.password = "";
        applicationStore.sendNotification("alert", "authentication-failed");
      });
    },
  },
});
