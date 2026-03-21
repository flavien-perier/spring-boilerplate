<template>
  <input type="text" class="form-control mb-2" :placeholder="$t('field.query')" v-model="query" @input="adminUsersStore.findUsers()">

  <table class="table">
    <thead>
    <tr>
      <th scope="col">{{ $t("field.user") }}</th>
      <th scope="col">{{ $t("field.actions") }}</th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="user in users">
      <td>{{ user.email }}</td>
      <td><a class="link-offset-1 cursor-pointer" @click="adminUsersStore.deleteUser(user.email)">
        <fio-icon icon="trash" />
        {{ $t("action.delete") }}
      </a></td>
    </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import {useAdminUsersStore} from "@/admin/admin-users/admin-users.store";
import {storeToRefs} from "pinia";

const adminUsersStore = useAdminUsersStore();
const { query, users } = storeToRefs(adminUsersStore);

adminUsersStore.init();
</script>