<template>
  <input type="text" class="form-control mb-2" :placeholder="$t('query')" v-model="query" @input="adminUsersStore.findUsers()">

  <table class="table">
    <thead>
    <tr>
      <th scope="col">{{ $t("user") }}</th>
      <th scope="col">{{ $t("actions") }}</th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="user in users">
      <td>{{ user.email }}</td>
      <td><a class="link-offset-1 cursor-pointer" @click="adminUsersStore.deleteUser(user.email)">
        <font-awesome-icon icon="trash" />
        {{ $t("delete") }}
      </a></td>
    </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import {useAdminUsersStore} from "@/admin/admin-users/admin-users.store.ts";
import {storeToRefs} from "pinia";

const adminUsersStore = useAdminUsersStore();
const { query, users } = storeToRefs(adminUsersStore);

adminUsersStore.init();
</script>