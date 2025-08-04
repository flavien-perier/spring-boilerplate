import { defineStore } from "pinia";
import {useApplicationStore} from "@/core/application.store";
import {userApi} from "@/core/util/api-util";
import {passwordUtil} from "@/core/util/password-util";

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
    buttonEnabled: (state) => state.isEmailValid && state.isPasswordValid && !state.computeAction,
  },
  actions: {
    init() {

    },

    close() {
      this.$reset();
    },

    createAccount() {
      if (!this.buttonEnabled) {
        return;
      }
      this.computeAction = true;

      userApi.createUser({
        email: this.email,
        password: this.password,
        proofOfWork: passwordUtil.proofOfWork(this.password, this.email),
      }).then(() => {
        applicationStore.sendNotification("info", "account-created");
        this.$router.push({ name: "login" });
      }).catch(applicationStore.axiosException).finally(() => { this.computeAction = false });
    },
  },
});
