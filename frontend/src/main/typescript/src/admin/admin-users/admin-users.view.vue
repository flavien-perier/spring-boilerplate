<template>
  <fio-input-text
    v-model="query"
    :placeholder="$t('field.query')"
    class="mb-xs"
    @input="adminUsersStore.findUsers()"
  />

  <fio-table
    :headers="headers"
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
      <tr v-for="user in users" :key="user.email">
        <td>
          <a
            class="link-offset-1 cursor-pointer"
            @click="
              router.push({
                name: 'admin-user',
                params: { userMail: user.email },
              })
            "
          >
            {{ user.email }}
          </a>
        </td>
        <td>
          <fio-icon :icon="user.enabled ? 'circle-check' : 'xmark'" />
        </td>
        <td>
          <a
            class="link-offset-1 cursor-pointer"
            @click="adminUsersStore.deleteUser(user.email)"
          >
            <fio-icon icon="trash" />
            {{ $t("action.delete") }}
          </a>
        </td>
      </tr>
    </template>
  </fio-table>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useAdminUsersStore } from "@/admin/admin-users/admin-users.store";
import { storeToRefs } from "pinia";
import type { TableHeader } from "@generated/component-library";

const { t } = useI18n();
const router = useRouter();

const headers = computed<TableHeader[]>(() => [
  {
    name: t("field.user"),
    key: "email",
    position: 0,
    sortable: true,
    show: true,
  },
  { name: t("field.enabled"), position: 1, sortable: false, show: true },
  { name: t("field.actions"), position: 2, sortable: false, show: true },
]);

const adminUsersStore = useAdminUsersStore();
const {
  query,
  users,
  currentPage,
  pageSize,
  totalPages,
  sortColumn,
  sortOrder,
} = storeToRefs(adminUsersStore);

function onPageChange(page: number) {
  adminUsersStore.setPage(page);
}

function onPageSizeChange(size: number) {
  adminUsersStore.setPageSize(size);
}

function onSortChange(payload: {
  key: string;
  direction: "asc" | "desc" | null;
}) {
  adminUsersStore.setSort(payload);
}

adminUsersStore.init();
</script>
