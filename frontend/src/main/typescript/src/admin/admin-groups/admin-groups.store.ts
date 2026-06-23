import { defineStore } from "pinia";
import { groupApi } from "@/core/util/api-util";
import { useApplicationStore } from "@/core/application.store";
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
    selectedGroupId: null as number | null,
    permissions: [] as PermissionSettingDto[],
    newGroupName: "",
    newGroupParentId: null as string | null,
    editGroupName: "",
    editGroupParentId: null as string | null,
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
        .findGroups()
        .then((response) => {
          this.groups = response.data;
        })
        .catch(applicationStore.axiosException);
    },
    selectGroup(groupId: number) {
      this.selectedGroupId = groupId;
      const group = this.selectedGroup;
      if (group) {
        this.editGroupName = group.name;
        this.editGroupParentId = group.parentId ? String(group.parentId) : null;
      }
      this.loadGroupPermissions(groupId);
    },
    createGroup() {
      const dto: GroupCreationDto = {
        name: this.newGroupName,
        parentId: this.newGroupParentId
          ? Number(this.newGroupParentId)
          : undefined,
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
        parentId: this.editGroupParentId
          ? Number(this.editGroupParentId)
          : undefined,
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
    deleteGroup(groupId: number) {
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
    loadGroupPermissions(groupId: number) {
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
