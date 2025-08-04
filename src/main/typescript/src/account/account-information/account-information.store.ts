import { defineStore } from "pinia";
import {userApi} from "@/core/util/api-util";
import {useApplicationStore} from "@/core/application.store";
import {passwordUtil} from "@/core/util/password-util";

const applicationStore = useApplicationStore();

export const useAccountInformationStore = defineStore("account-information", {
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
      this.email = applicationStore.user?.email || "";
    },

    close() {
      this.$reset();
    },

    update() {
      if (!this.buttonEnabled) {
        return;
      }
      this.computeAction = true;

      userApi.updateUserMe({
        email: this.email,
        password: this.password,
        proofOfWork: passwordUtil.proofOfWork(this.password, this.email),
      }).then(() => {
        applicationStore.sendNotification("info", "account-updated");
        this.password = "";
      }).catch(applicationStore.axiosException).finally(() => { this.computeAction = false });
    },
  },
});
