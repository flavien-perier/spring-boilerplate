import { defineStore } from "pinia";
import { groupApi, userApi } from "@/core/util/api-util";
import { useApplicationStore } from "@/core/application.store";
import type {
  GroupDto,
  PermissionSettingDto,
  UserDto,
  UserUpdateAdminDto,
} from "@generated/api";

const applicationStore = useApplicationStore();

export const useAdminUserStore = defineStore("admin-user", {
  state: () => ({
    user: null as UserDto | null,
    userMail: "",
    groups: [] as GroupDto[],
    userGroups: [] as GroupDto[],
    permissionOverrides: [] as PermissionSettingDto[],
    editEmail: "",
    editPassword: "",
    editProofOfWork: "",
  }),
  getters: {},
  actions: {
    init(userMail: string) {
      this.userMail = userMail;
      this.loadUser();
      this.loadAllGroups();
      this.loadUserGroups();
      this.loadPermissionOverrides();
    },
    loadUser() {
      userApi
        .getUser(this.userMail)
        .then((response) => {
          this.user = response.data;
          this.editEmail = response.data.email;
        })
        .catch(applicationStore.axiosException);
    },
    loadAllGroups() {
      groupApi
        .findGroups(1, 1000)
        .then((response) => {
          this.groups = response.data.content;
        })
        .catch(applicationStore.axiosException);
    },
    loadUserGroups() {
      userApi
        .getUserGroups(this.userMail, 1, 1000)
        .then((response) => {
          this.userGroups = response.data.content;
        })
        .catch(applicationStore.axiosException);
    },
    loadPermissionOverrides() {
      userApi
        .getUserPermissionOverrides(this.userMail, 1, 100)
        .then((response) => {
          this.permissionOverrides = response.data.content;
        })
        .catch(applicationStore.axiosException);
    },
    updateUser() {
      const dto: UserUpdateAdminDto = {
        email: this.editEmail || undefined,
        password: this.editPassword || undefined,
        proofOfWork: this.editProofOfWork || undefined,
      };
      userApi
        .updateUser(this.userMail, dto)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.user-updated"
          );
          this.editPassword = "";
          this.editProofOfWork = "";
          this.loadUser();
        })
        .catch(applicationStore.axiosException);
    },
    setEnabled(enabled: boolean) {
      const dto: UserUpdateAdminDto = { enabled };
      userApi
        .updateUser(this.userMail, dto)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.user-updated"
          );
          this.loadUser();
        })
        .catch(applicationStore.axiosException);
    },
    resetOtp() {
      userApi
        .disableUserOtp(this.userMail)
        .then(() => {
          applicationStore.sendNotification("info", "notification.otp-reset");
          this.loadUser();
        })
        .catch(applicationStore.axiosException);
    },
    sendPasswordReset() {
      userApi
        .sendUserPasswordReset(this.userMail)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.password-reset-sent"
          );
        })
        .catch(applicationStore.axiosException);
    },
    deleteUser() {
      userApi
        .deleteUser(this.userMail)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.user-deleted"
          );
          this.$router.push({ name: "admin-users" });
        })
        .catch(applicationStore.axiosException);
    },
    isUserInGroup(groupId: string): boolean {
      return this.userGroups.some((g) => g.id === groupId);
    },
    toggleUserGroup(groupId: string) {
      if (this.isUserInGroup(groupId)) {
        userApi
          .removeUserFromGroup(this.userMail, groupId)
          .then(() => this.loadUserGroups())
          .catch(applicationStore.axiosException);
      } else {
        userApi
          .addUserToGroup(this.userMail, groupId)
          .then(() => this.loadUserGroups())
          .catch(applicationStore.axiosException);
      }
    },
    setUserPermission(permission: string, allow: boolean) {
      userApi
        .setUserPermission(this.userMail, permission, { allow })
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.permission-updated"
          );
          this.loadPermissionOverrides();
        })
        .catch(applicationStore.axiosException);
    },
    removeUserPermission(permission: string) {
      userApi
        .removeUserPermission(this.userMail, permission)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.permission-updated"
          );
          this.loadPermissionOverrides();
        })
        .catch(applicationStore.axiosException);
    },
  },
});
