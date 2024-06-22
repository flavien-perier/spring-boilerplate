import { defineStore } from "pinia";
import {useApplicationStore} from "@/core/application.store";
import {userApi} from "@/core/util/api-util";
import {passwordUtil} from "@/core/util/password-util";

const applicationStore = useApplicationStore();

export const useCreateAccountStore = defineStore("create-account", {
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

    },

    close() {
      this.$reset();
    },

    createAccount() {
      userApi.createUser({
        email: this.email,
        password: passwordUtil.encode(this.password, this.email),
      }).then(() => {
        applicationStore.sendNotification("info", "account-created");
        this.$router.push({ name: "login" });
      }).catch(applicationStore.axiosException);
    },
  },
});
