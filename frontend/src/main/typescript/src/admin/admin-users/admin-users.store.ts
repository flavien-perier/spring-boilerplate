import { defineStore } from "pinia";
import { userApi } from "@/core/util/api-util";
import { useApplicationStore } from "@/core/application.store";
import {
  passwordUtil,
  type TableSortEvent,
} from "@generated/component-library";
import type { UserDto } from "@generated/api";

const applicationStore = useApplicationStore();

export const useAdminUsersStore = defineStore("admin-users", {
  state: () => ({
    users: [] as UserDto[],
    query: "",
    currentPage: 1,
    pageSize: 10,
    totalElements: 0,
    totalPages: 1,
    sortColumn: "email",
    sortOrder: "asc" as "asc" | "desc",
  }),
  getters: {},
  actions: {
    init() {
      this.findUsers();
    },
    findUsers() {
      userApi
        .findUsers(
          this.query,
          this.currentPage,
          this.pageSize,
          this.sortColumn,
          this.sortOrder
        )
        .then((response) => {
          this.users = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
        })
        .catch(applicationStore.axiosException);
    },
    setPage(page: number) {
      this.currentPage = page;
      this.findUsers();
    },
    setPageSize(size: number) {
      this.pageSize = size;
      this.currentPage = 1;
      this.findUsers();
    },
    setSort(payload: TableSortEvent) {
      this.sortColumn = payload.key;
      this.sortOrder = payload.direction ?? "asc";
      this.currentPage = 1;
      this.findUsers();
    },
    deleteUser(email: string) {
      userApi
        .deleteUser(email)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.user-deleted"
          );
          this.findUsers();
        })
        .catch(applicationStore.axiosException);
    },
  },
});
