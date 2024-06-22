import { defineStore } from "pinia";
import {userApi} from "@/core/util/api-util";
import {useApplicationStore} from "@/core/application.store";
import {passwordUtil} from "@/core/util/password-util";

const applicationStore = useApplicationStore();

export const useAccountInformationStore = defineStore("account-information", {
  state: () => ({
    email: "",
    password: "",
    repeatPassword: "",
  }),
  getters: {
    buttonEnabled: (state) =>
      state.email !== "" &&
      state.password !== "" &&
      state.password === state.repeatPassword
  },
  actions: {
    init() {
      this.email = applicationStore.user?.email || "";
    },

    close() {
      this.$reset();
    },

    update() {
      userApi.updateUserMe({
        email: this.email,
        password: passwordUtil.encode(this.password, this.email),
      }).then(() => {
        applicationStore.sendNotification("info", "account-updated");
        this.password = "";
        this.repeatPassword = "";
      }).catch(applicationStore.axiosException);
    },
  },
});
