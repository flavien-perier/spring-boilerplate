<template>
  <section class="account-view">
    <h1>{{ $t("menu.account") }}</h1>
    <fio-tab-layout :tabs="accountTabs">
      <RouterView />
    </fio-tab-layout>
  </section>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { RouterView, useRouter, useRoute } from "vue-router";
import type { TabElement } from "@generated/component-library";

const router = useRouter();
const route = useRoute();

const accountTabs = computed<TabElement[]>(() => [
  {
    i18nKey: "menu.information",
    isActive: route.name === "account-information",
    action: () => router.push({ name: "account-information" }),
  },
  {
    i18nKey: "menu.security",
    isActive: route.name === "account-security",
    action: () => router.push({ name: "account-security" }),
  },
]);
</script>

<style scoped lang="scss">
.account-view {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1rem;
}
</style>
