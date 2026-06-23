<template>
  <fio-split-layout align="stretch">
    <template #left>
      <div class="admin-groups__header mb-s">
        <h3>{{ $t("menu.groups") }}</h3>
        <fio-input-button
          :label="$t('action.create-group')"
          size="s"
          @click="openCreateModal"
        />
      </div>

      <fio-table :headers="groupHeaders">
        <template #body>
          <tr
            v-for="group in groups"
            :key="group.id"
            :class="{ 'table-active': group.id === selectedGroupId }"
          >
            <td>
              <a
                class="link-offset-1 cursor-pointer"
                @click="adminGroupsStore.selectGroup(group.id)"
              >
                {{ group.name }}
              </a>
            </td>
            <td>{{ group.parentName ?? "-" }}</td>
          </tr>
        </template>
      </fio-table>
    </template>

    <template #right>
      <template v-if="selectedGroup">
        <h3 class="mb-s">
          {{ $t("field.permission") }} — {{ selectedGroup.name }}
        </h3>

        <div class="mb-s">
          <fio-input-text
            v-model="editGroupName"
            :placeholder="$t('field.group')"
            class="mb-xs"
          />
          <fio-input-select
            v-model="editGroupParentId"
            :options="nonDescendantOptions"
            :null-option="$t('field.no-parent')"
            class="mb-xs"
          />
          <fio-input-button
            :label="$t('action.update')"
            :disabled="!editGroupName"
            @click="adminGroupsStore.updateGroup()"
          />
        </div>

        <fio-permission-setting
          v-for="setting in permissions"
          :key="setting.permission"
          v-model:override="overrideFlags[setting.permission]"
          v-model:allow="allowFlags[setting.permission]"
          :permission="setting.permission"
          :override-label="$t('field.override')"
          :allow-label="$t('field.allow')"
          :deny-label="$t('field.deny')"
          :locked="setting.locked"
          class="mb-xs"
          @change="onPermissionChange"
        />

        <fio-input-button
          :label="$t('action.delete')"
          variant="danger"
          size="s"
          class="mt-l"
          @click="adminGroupsStore.deleteGroup(selectedGroup.id)"
        />
      </template>
      <div v-else class="admin-groups__empty">
        <fio-icon icon="users" />
        <p class="mt-xs">{{ $t("admin.select-group") }}</p>
      </div>
    </template>
  </fio-split-layout>

  <fio-modal
    :show="showCreateModal"
    :title="$t('action.create-group')"
    :reject-message="$t('action.cancel')"
    :resolve-message="$t('action.create')"
    :resolve-disabled="!newGroupName"
    @close="closeCreateModal"
    @resolve="confirmCreateGroup"
  >
    <fio-input-text
      v-model="newGroupName"
      :placeholder="$t('field.group')"
      class="mb-xs"
    />
    <fio-input-select
      v-model="newGroupParentId"
      :options="groupOptions"
      :null-option="$t('field.no-parent')"
    />
  </fio-modal>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import { computed, reactive, ref, watch } from "vue";
import { useAdminGroupsStore } from "@/admin/admin-groups/admin-groups.store";
import { storeToRefs } from "pinia";
import type { SelectOption, TableHeader } from "@generated/component-library";

const { t } = useI18n();

const groupHeaders = computed<TableHeader[]>(() => [
  { name: t("field.group"), position: 0, sortable: false, show: true },
  { name: t("field.parent"), position: 1, sortable: false, show: true },
]);

const adminGroupsStore = useAdminGroupsStore();
const {
  groups,
  selectedGroupId,
  permissions,
  newGroupName,
  newGroupParentId,
  editGroupName,
  editGroupParentId,
} = storeToRefs(adminGroupsStore);

adminGroupsStore.init();

const selectedGroup = computed(() => adminGroupsStore.selectedGroup);

const showCreateModal = ref(false);

function openCreateModal() {
  newGroupName.value = "";
  newGroupParentId.value = null;
  showCreateModal.value = true;
}

function closeCreateModal() {
  showCreateModal.value = false;
}

function confirmCreateGroup() {
  if (!newGroupName.value) return;
  adminGroupsStore.createGroup();
  showCreateModal.value = false;
}

const groupOptions = computed<SelectOption[]>(() =>
  groups.value.map((group) => ({ value: String(group.id), label: group.name }))
);

const nonDescendantGroups = computed(() =>
  groups.value.filter((group) => group.id !== selectedGroupId.value)
);

const nonDescendantOptions = computed<SelectOption[]>(() =>
  nonDescendantGroups.value.map((group) => ({
    value: String(group.id),
    label: group.name,
  }))
);

const overrideFlags = reactive<Record<string, boolean>>({});
const allowFlags = reactive<Record<string, boolean>>({});

watch(permissions, (newPermissions) => {
  newPermissions.forEach((setting) => {
    if (setting.locked) {
      overrideFlags[setting.permission] = true;
      allowFlags[setting.permission] = setting.inheritedAllow ?? false;
    } else {
      overrideFlags[setting.permission] =
        setting.allow !== undefined && setting.allow !== null;
      allowFlags[setting.permission] = setting.allow ?? false;
    }
  });
});

function onPermissionChange({
  permission,
  override,
  allow,
}: {
  permission: string;
  override: boolean;
  allow: boolean;
}) {
  if (override) {
    adminGroupsStore.setGroupPermission(permission, allow);
  } else {
    adminGroupsStore.removeGroupPermission(permission);
  }
}
</script>

<style scoped lang="scss">
.admin-groups__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.admin-groups__empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  color: var(--secondary-darker-30);
}
</style>
