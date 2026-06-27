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

      <fio-table
        :headers="groupHeaders"
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
  {
    name: t("field.group"),
    key: "name",
    position: 0,
    sortable: true,
    show: true,
  },
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
  currentPage,
  pageSize,
  totalPages,
  sortColumn,
  sortOrder,
} = storeToRefs(adminGroupsStore);

function onPageChange(page: number) {
  adminGroupsStore.setPage(page);
}

function onPageSizeChange(size: number) {
  adminGroupsStore.setPageSize(size);
}

function onSortChange(payload: {
  key: string;
  direction: "asc" | "desc" | null;
}) {
  adminGroupsStore.setSort(payload);
}

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
    adminGroupsStore.removeGroupPermission(permission);
  } else {
    adminGroupsStore.setGroupPermission(permission, value);
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
