<template>
  <fio-input-text
    v-model="query"
    :placeholder="$t('field.query')"
    class="admin-users-view__actions"
    @input="adminUsersStore.findUsers()"
  />

  <fio-table :headers="headers">
    <template #body>
      <tr v-for="user in users" :key="user.email">
        <td>{{ user.email }}</td>
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
import { useAdminUsersStore } from "@/admin/admin-users/admin-users.store";
import { storeToRefs } from "pinia";
import type { TableHeader } from "@generated/component-library";

const { t } = useI18n();

const headers = computed<TableHeader[]>(() => [
  { name: t("field.user"), position: 0, sortable: false, show: true },
  { name: t("field.actions"), position: 1, sortable: false, show: true },
]);

const adminUsersStore = useAdminUsersStore();
const { query, users } = storeToRefs(adminUsersStore);

adminUsersStore.init();
</script>

<style scoped lang="scss">
.admin-users-view__actions {
  margin-bottom: 0.5rem;
}
</style>
