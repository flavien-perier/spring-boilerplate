<template>
  <fio-auth-layout
    name="undraw_create_account"
    :title="$t('menu.create-account')"
  >
    <fio-input-email
      class="mb-xl"
      v-model="email"
      @update:isValid="(value) => (isEmailValid = value)"
    />

    <fio-input-create-password
      class="mb-xl"
      v-model="password"
      :min-password-length="applicationStore.configuration.minPasswordLength"
      @update:isValid="(value) => (isPasswordValid = value)"
    />

    <fio-input-button
      :label="$t('action.create')"
      :disabled="!buttonEnabled"
      @click="createAccountStore.createAccount"
    />
  </fio-auth-layout>
</template>

<script setup lang="ts">
import { useCreateAccountStore } from "@/create-account/create-account.store";
import { useApplicationStore } from "@/core/application.store";
import { storeToRefs } from "pinia";
import { onBeforeRouteLeave } from "vue-router";

const createAccountStore = useCreateAccountStore();
const applicationStore = useApplicationStore();
const { email, password, isPasswordValid, isEmailValid, buttonEnabled } =
  storeToRefs(createAccountStore);

createAccountStore.init();

onBeforeRouteLeave(createAccountStore.close);
</script>
