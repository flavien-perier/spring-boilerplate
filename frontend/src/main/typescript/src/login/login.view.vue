<template>
  <fio-auth-layout name="undraw_login">
    <h1 class="auth-subtitle">{{ $t("menu.login") }}</h1>

    <fio-input-email
      class="mb-xl"
      v-model="email"
      @update:isValid="(value) => (isEmailValid = value)"
    />

    <fio-input-password
      class="mb-xl"
      v-model="password"
      :label="$t('field.password')"
      @keyup.enter="loginStore.login"
    />
    <router-link :to="{ name: 'forgot-password' }">{{
      $t("action.forgot-password")
    }}</router-link>

    <fio-input-button
      :label="$t('action.connect')"
      :disabled="!buttonEnabled"
      @click="loginStore.login"
    />

    <router-link :to="{ name: 'create-account' }">{{
      $t("menu.create-account")
    }}</router-link>
  </fio-auth-layout>
</template>

<script setup lang="ts">
import { useLoginStore } from "@/login/login.store";
import { storeToRefs } from "pinia";
import { onBeforeRouteLeave, useRoute } from "vue-router";

const loginStore = useLoginStore();
const { email, password, isEmailValid, buttonEnabled } =
  storeToRefs(loginStore);

const route = useRoute();
loginStore.init(route.query?.activationToken as string | undefined);

onBeforeRouteLeave(loginStore.close);
</script>

<style scoped lang="scss">
.auth-subtitle {
  text-align: center;
  margin-bottom: 1.5rem;
}
</style>
