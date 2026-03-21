<template>
  <fio-auth-layout name="undraw_authentication">
    <h1 class="auth-subtitle">{{ $t("action.change-password") }}</h1>

    <fio-input-create-password
      class="mb-xl"
      v-model="password"
      :min-password-length="applicationStore.configuration.minPasswordLength"
      @update:isValid="(value) => (isPasswordValid = value)"
      @keyup.enter="changePasswordStore.update"
    />

    <fio-input-button
      :label="$t('action.update')"
      :disabled="!buttonEnabled"
      @click="changePasswordStore.update"
    />
  </fio-auth-layout>
</template>

<script setup lang="ts">
import { storeToRefs } from "pinia";
import { onBeforeRouteLeave, useRoute } from "vue-router";
import { useChangePasswordStore } from "@/change-password/change-password.store";
import { useApplicationStore } from "@/core/application.store";

const changePasswordStore = useChangePasswordStore();
const applicationStore = useApplicationStore();
const { password, buttonEnabled, isPasswordValid } =
  storeToRefs(changePasswordStore);

const route = useRoute();
changePasswordStore.init(
  route.query?.email as string | undefined,
  route.query?.forgotPasswordToken as string | undefined
);

onBeforeRouteLeave(changePasswordStore.close);
</script>

<style scoped lang="scss">
.auth-subtitle {
  text-align: center;
  margin-bottom: 1.5rem;
}
</style>
