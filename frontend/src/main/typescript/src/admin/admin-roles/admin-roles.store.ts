import { defineStore } from "pinia";
import { roleApi } from "@/core/util/api-util";
import { useApplicationStore } from "@/core/application.store";
import type { TableSortEvent } from "@generated/component-library";
import type {
  RoleCreationDto,
  RoleDto,
  RoleUpdateDto,
  PermissionSettingDto,
} from "@generated/api";

const applicationStore = useApplicationStore();

export const useAdminRolesStore = defineStore("admin-roles", {
  state: () => ({
    roles: [] as RoleDto[],
    selectedRoleId: null as string | null,
    permissions: [] as PermissionSettingDto[],
    newRoleName: "",
    newRoleParentId: null as string | null,
    editRoleName: "",
    editRoleParentId: null as string | null,
    currentPage: 1,
    pageSize: 10,
    totalElements: 0,
    totalPages: 1,
    sortColumn: "name",
    sortOrder: "asc" as "asc" | "desc",
  }),
  getters: {
    selectedRole(): RoleDto | null {
      return this.roles.find((g) => g.id === this.selectedRoleId) ?? null;
    },
  },
  actions: {
    init() {
      this.findRoles();
    },
    findRoles() {
      roleApi
        .findRoles(
          this.currentPage,
          this.pageSize,
          this.sortColumn,
          this.sortOrder
        )
        .then((response) => {
          this.roles = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
        })
        .catch(applicationStore.axiosException);
    },
    setPage(page: number) {
      this.currentPage = page;
      this.findRoles();
    },
    setPageSize(size: number) {
      this.pageSize = size;
      this.currentPage = 1;
      this.findRoles();
    },
    setSort(payload: TableSortEvent) {
      this.sortColumn = payload.key;
      this.sortOrder = payload.direction ?? "asc";
      this.currentPage = 1;
      this.findRoles();
    },
    selectRole(roleId: string) {
      this.selectedRoleId = roleId;
      const role = this.selectedRole;
      if (role) {
        this.editRoleName = role.name;
        this.editRoleParentId = role.parentId ?? null;
      }
      this.loadRolePermissions(roleId);
    },
    createRole() {
      const dto: RoleCreationDto = {
        name: this.newRoleName,
        parentId: this.newRoleParentId ?? undefined,
      };
      roleApi
        .createRole(dto)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.role-created"
          );
          this.newRoleName = "";
          this.newRoleParentId = null;
          this.findRoles();
        })
        .catch(applicationStore.axiosException);
    },
    updateRole() {
      if (this.selectedRoleId === null) return;
      const dto: RoleUpdateDto = {
        name: this.editRoleName || undefined,
        parentId: this.editRoleParentId ?? undefined,
      };
      roleApi
        .updateRole(this.selectedRoleId, dto)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.role-updated"
          );
          this.findRoles();
        })
        .catch(applicationStore.axiosException);
    },
    deleteRole(roleId: string) {
      roleApi
        .deleteRole(roleId)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.role-deleted"
          );
          if (this.selectedRoleId === roleId) {
            this.selectedRoleId = null;
            this.permissions = [];
          }
          this.findRoles();
        })
        .catch(applicationStore.axiosException);
    },
    loadRolePermissions(roleId: string) {
      roleApi
        .getRolePermissions(roleId)
        .then((response) => {
          this.permissions = response.data;
        })
        .catch(applicationStore.axiosException);
    },
    setRolePermission(permission: string, allow: boolean) {
      if (this.selectedRoleId === null) return;
      roleApi
        .setRolePermission(this.selectedRoleId, permission, { allow })
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.permission-updated"
          );
          this.loadRolePermissions(this.selectedRoleId!);
        })
        .catch(applicationStore.axiosException);
    },
    removeRolePermission(permission: string) {
      if (this.selectedRoleId === null) return;
      roleApi
        .removeRolePermission(this.selectedRoleId, permission)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.permission-updated"
          );
          this.loadRolePermissions(this.selectedRoleId!);
        })
        .catch(applicationStore.axiosException);
    },
  },
});
