<template>
  <fio-auth-layout
    name="undraw_forgot_password"
    :title="$t('action.forgot-password')"
  >
    <fio-input-email
      class="mb-xl"
      v-model="email"
      @update:isValid="(value) => (isEmailValid = value)"
      @keyup.enter="forgotPasswordStore.send"
    />

    <fio-input-button
      :label="$t('action.send')"
      :disabled="!buttonEnabled"
      :waiting="computeAction"
      @click="forgotPasswordStore.send"
    />
  </fio-auth-layout>
</template>

<script setup lang="ts">
import { useForgotPasswordStore } from "@/forgot-password/forgot-password.store";
import { storeToRefs } from "pinia";
import { onBeforeRouteLeave } from "vue-router";

const forgotPasswordStore = useForgotPasswordStore();
const { email, buttonEnabled, isEmailValid, computeAction } = storeToRefs(forgotPasswordStore);

forgotPasswordStore.init();

onBeforeRouteLeave(forgotPasswordStore.close);
</script>
