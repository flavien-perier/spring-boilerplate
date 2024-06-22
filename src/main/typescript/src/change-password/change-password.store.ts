import { defineStore } from "pinia";
import {useApplicationStore} from "@/core/application.store";
import {userApi} from "@/core/util/api-util";
import {passwordUtil} from "@/core/util/password-util";

const applicationStore = useApplicationStore();

export const useChangePasswordStore = defineStore("change-password", {
  state: () => ({
    email: "",
    token: "",
    password: "",
    repeatPassword: "",
  }),
  getters: {
    buttonEnabled: (state) =>
      state.password !== "" &&
      state.password === state.repeatPassword
  },
  actions: {
    init(email?: string, token?: string) {
      this.email = email || "";
      this.token = token || "";
    },

    close() {
      this.$reset();
    },

    update() {
      userApi.updatePassword({
        token: this.token,
        password: passwordUtil.encode(this.password, this.email),
      }).then(() => {
        applicationStore.sendNotification("info", "password-updated");
        this.$router.push({ name: "login" });
      }).catch(applicationStore.axiosException);
    },
  },
});
