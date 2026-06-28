<template>
  <fio-split-layout align="stretch">
    <template #left>
      <div class="admin-roles__header mb-s">
        <h3>{{ $t("menu.roles") }}</h3>
        <fio-input-button
          :label="$t('action.create-role')"
          size="s"
          @click="openCreateModal"
        />
      </div>

      <fio-table
        :headers="roleHeaders"
        :current-page="currentPage"
        :page-size="pageSize"
        :total-pages="totalPages"
        :sort-key="sortColumn"
        :sort-direction="sortOrder"
        @page-change="onPageChange"
        @page-size-change="onPageSizeChange"
        @sort-change="onSortChange"
      >
        <template #body>
          <tr
            v-for="role in roles"
            :key="role.id"
            :class="{ 'table-active': role.id === selectedRoleId }"
          >
            <td>
              <a
                class="link-offset-1 cursor-pointer"
                @click="adminRolesStore.selectRole(role.id)"
              >
                {{ role.name }}
              </a>
            </td>
            <td>{{ role.parentName ?? "-" }}</td>
          </tr>
        </template>
      </fio-table>
    </template>

    <template #right>
      <template v-if="selectedRole">
        <h3 class="mb-s">
          {{ $t("field.permission") }} — {{ selectedRole.name }}
        </h3>

        <div class="mb-s">
          <fio-input-text
            v-model="editRoleName"
            :placeholder="$t('field.role')"
            class="mb-xs"
          />
          <fio-input-select
            v-model="editRoleParentId"
            :options="nonDescendantOptions"
            :null-option="$t('field.no-parent')"
            class="mb-xs"
          />
          <fio-input-button
            :label="$t('action.update')"
            :disabled="!editRoleName"
            @click="adminRolesStore.updateRole()"
          />
        </div>

        <fio-input-tri-toggle
          v-for="setting in permissions"
          :key="setting.permission"
          v-model="triValues[setting.permission]"
          :label="setting.permission"
          :disabled="setting.locked"
          class="mb-xs"
          @change="onPermissionChange(setting.permission, $event)"
        />

        <fio-input-button
          :label="$t('action.delete')"
          variant="danger"
          size="s"
          class="mt-l"
          @click="adminRolesStore.deleteRole(selectedRole.id)"
        />
      </template>
      <div v-else class="admin-roles__empty">
        <fio-icon icon="users" />
        <p class="mt-xs">{{ $t("admin.select-role") }}</p>
      </div>
    </template>
  </fio-split-layout>

  <fio-modal
    :show="showCreateModal"
    :title="$t('action.create-role')"
    :reject-message="$t('action.cancel')"
    :resolve-message="$t('action.create')"
    :resolve-disabled="!newRoleName"
    @close="closeCreateModal"
    @resolve="confirmCreateRole"
  >
    <fio-input-text
      v-model="newRoleName"
      :placeholder="$t('field.role')"
      class="mb-xs"
    />
    <fio-input-select
      v-model="newRoleParentId"
      :options="roleOptions"
      :null-option="$t('field.no-parent')"
    />
  </fio-modal>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import { computed, reactive, ref, watch } from "vue";
import { useAdminRolesStore } from "@/admin/admin-roles/admin-roles.store";
import { storeToRefs } from "pinia";
import type { SelectOption, TableHeader } from "@generated/component-library";

const { t } = useI18n();

const roleHeaders = computed<TableHeader[]>(() => [
  {
    name: t("field.role"),
    key: "name",
    position: 0,
    sortable: true,
    show: true,
  },
  { name: t("field.parent"), position: 1, sortable: false, show: true },
]);

const adminRolesStore = useAdminRolesStore();
const {
  roles,
  selectedRoleId,
  permissions,
  newRoleName,
  newRoleParentId,
  editRoleName,
  editRoleParentId,
  currentPage,
  pageSize,
  totalPages,
  sortColumn,
  sortOrder,
} = storeToRefs(adminRolesStore);

function onPageChange(page: number) {
  adminRolesStore.setPage(page);
}

function onPageSizeChange(size: number) {
  adminRolesStore.setPageSize(size);
}

function onSortChange(payload: {
  key: string;
  direction: "asc" | "desc" | null;
}) {
  adminRolesStore.setSort(payload);
}

adminRolesStore.init();

const selectedRole = computed(() => adminRolesStore.selectedRole);

const showCreateModal = ref(false);

function openCreateModal() {
  newRoleName.value = "";
  newRoleParentId.value = null;
  showCreateModal.value = true;
}

function closeCreateModal() {
  showCreateModal.value = false;
}

function confirmCreateRole() {
  if (!newRoleName.value) return;
  adminRolesStore.createRole();
  showCreateModal.value = false;
}

const roleOptions = computed<SelectOption[]>(() =>
  roles.value.map((role) => ({ value: String(role.id), label: role.name }))
);

const nonDescendantRoles = computed(() =>
  roles.value.filter((role) => role.id !== selectedRoleId.value)
);

const nonDescendantOptions = computed<SelectOption[]>(() =>
  nonDescendantRoles.value.map((role) => ({
    value: String(role.id),
    label: role.name,
  }))
);

const triValues = reactive<Record<string, boolean | null>>({});

watch(
  permissions,
  (newPermissions) => {
    newPermissions.forEach((setting) => {
      if (setting.locked) {
        triValues[setting.permission] = setting.inheritedAllow ?? false;
      } else {
        triValues[setting.permission] = setting.allow ?? null;
      }
    });
  },
  { immediate: true }
);

function onPermissionChange(permission: string, value: boolean | null) {
  if (value === null) {
    adminRolesStore.removeRolePermission(permission);
  } else {
    adminRolesStore.setRolePermission(permission, value);
  }
}
</script>

<style scoped lang="scss">
.admin-roles__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.admin-roles__empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  color: var(--secondary-darker-30);
}
</style>
