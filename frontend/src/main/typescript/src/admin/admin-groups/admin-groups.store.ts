import { defineStore } from "pinia";
import { groupApi } from "@/core/util/api-util";
import { useApplicationStore } from "@/core/application.store";
import type { TableSortEvent } from "@generated/component-library";
import type {
  GroupCreationDto,
  GroupDto,
  GroupUpdateDto,
  PermissionSettingDto,
} from "@generated/api";

const applicationStore = useApplicationStore();

export const useAdminGroupsStore = defineStore("admin-groups", {
  state: () => ({
    groups: [] as GroupDto[],
    selectedGroupId: null as string | null,
    permissions: [] as PermissionSettingDto[],
    newGroupName: "",
    newGroupParentId: null as string | null,
    editGroupName: "",
    editGroupParentId: null as string | null,
    currentPage: 1,
    pageSize: 10,
    totalElements: 0,
    totalPages: 1,
    sortColumn: "name",
    sortOrder: "asc" as "asc" | "desc",
  }),
  getters: {
    selectedGroup(): GroupDto | null {
      return this.groups.find((g) => g.id === this.selectedGroupId) ?? null;
    },
  },
  actions: {
    init() {
      this.findGroups();
    },
    findGroups() {
      groupApi
        .findGroups(
          this.currentPage,
          this.pageSize,
          this.sortColumn,
          this.sortOrder
        )
        .then((response) => {
          this.groups = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
        })
        .catch(applicationStore.axiosException);
    },
    setPage(page: number) {
      this.currentPage = page;
      this.findGroups();
    },
    setPageSize(size: number) {
      this.pageSize = size;
      this.currentPage = 1;
      this.findGroups();
    },
    setSort(payload: TableSortEvent) {
      this.sortColumn = payload.key;
      this.sortOrder = payload.direction ?? "asc";
      this.currentPage = 1;
      this.findGroups();
    },
    selectGroup(groupId: string) {
      this.selectedGroupId = groupId;
      const group = this.selectedGroup;
      if (group) {
        this.editGroupName = group.name;
        this.editGroupParentId = group.parentId ?? null;
      }
      this.loadGroupPermissions(groupId);
    },
    createGroup() {
      const dto: GroupCreationDto = {
        name: this.newGroupName,
        parentId: this.newGroupParentId ?? undefined,
      };
      groupApi
        .createGroup(dto)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.group-created"
          );
          this.newGroupName = "";
          this.newGroupParentId = null;
          this.findGroups();
        })
        .catch(applicationStore.axiosException);
    },
    updateGroup() {
      if (this.selectedGroupId === null) return;
      const dto: GroupUpdateDto = {
        name: this.editGroupName || undefined,
        parentId: this.editGroupParentId ?? undefined,
      };
      groupApi
        .updateGroup(this.selectedGroupId, dto)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.group-updated"
          );
          this.findGroups();
        })
        .catch(applicationStore.axiosException);
    },
    deleteGroup(groupId: string) {
      groupApi
        .deleteGroup(groupId)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.group-deleted"
          );
          if (this.selectedGroupId === groupId) {
            this.selectedGroupId = null;
            this.permissions = [];
          }
          this.findGroups();
        })
        .catch(applicationStore.axiosException);
    },
    loadGroupPermissions(groupId: string) {
      groupApi
        .getGroupPermissions(groupId)
        .then((response) => {
          this.permissions = response.data;
        })
        .catch(applicationStore.axiosException);
    },
    setGroupPermission(permission: string, allow: boolean) {
      if (this.selectedGroupId === null) return;
      groupApi
        .setGroupPermission(this.selectedGroupId, permission, { allow })
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.permission-updated"
          );
          this.loadGroupPermissions(this.selectedGroupId!);
        })
        .catch(applicationStore.axiosException);
    },
    removeGroupPermission(permission: string) {
      if (this.selectedGroupId === null) return;
      groupApi
        .removeGroupPermission(this.selectedGroupId, permission)
        .then(() => {
          applicationStore.sendNotification(
            "info",
            "notification.permission-updated"
          );
          this.loadGroupPermissions(this.selectedGroupId!);
        })
        .catch(applicationStore.axiosException);
    },
  },
});
