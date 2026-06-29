import { defineStore } from "pinia";
import { roleApi, userApi } from "@/core/util/api-util";
import { useApplicationStore } from "@/core/application.store";
import type {
  RoleDto,
  PermissionSettingDto,
  UserDto,
  UserUpdateAdminDto,
} from "@generated/api";

const applicationStore = useApplicationStore();

export const useAdminUserStore = defineStore("admin-user", {
  state: () => ({
    user: null as UserDto | null,
    userMail: "",
    roles: [] as RoleDto[],
    userRoles: [] as RoleDto[],
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
      this.loadAllRoles();
      this.loadUserRoles();
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
    loadAllRoles() {
      roleApi
        .findRoles(1, 1000)
        .then((response) => {
          this.roles = response.data.content;
        })
        .catch(applicationStore.axiosException);
    },
    loadUserRoles() {
      userApi
        .getUserRoles(this.userMail, 1, 1000)
        .then((response) => {
          this.userRoles = response.data.content;
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
      applicationStore
        .showModal(
          "modal.are-you-sure",
          "modal.user-deletion-validation",
          "modal.yes",
          "modal.no"
        )
        .then(() => {
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
        })
        .catch(() => {});
    },
    isUserInRole(roleId: string): boolean {
      return this.userRoles.some((g) => g.id === roleId);
    },
    toggleUserRole(roleId: string) {
      if (this.isUserInRole(roleId)) {
        userApi
          .removeUserFromRole(this.userMail, roleId)
          .then(() => this.loadUserRoles())
          .catch(applicationStore.axiosException);
      } else {
        userApi
          .addUserToRole(this.userMail, roleId)
          .then(() => this.loadUserRoles())
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
