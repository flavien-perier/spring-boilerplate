import { defineStore } from "pinia";
import {userApi} from "@/core/util/api-util";
import {useApplicationStore} from "@/core/application.store";
import {passwordUtil} from "@/core/util/password-util";
import type {UserDto} from "api-generated";

const applicationStore = useApplicationStore();

export const useAdminUsersStore = defineStore("admin-users", {
  state: () => ({
    users: [] as UserDto[],
    query: "",
  }),
  getters: {},
  actions: {
    init() {
      this.findUsers();
    },
    findUsers() {
      userApi.findUsers(this.query)
        .then((response) => {
          this.users = response.data.content;
        }).catch(applicationStore.axiosException);
    },
    deleteUser(email: string) {
      userApi.deleteUser(email)
        .then(this.findUsers)
        .catch(applicationStore.axiosException);
    },
  },
});
