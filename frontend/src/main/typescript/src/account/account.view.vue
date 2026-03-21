<template>
  <fio-page-view :title="$t('menu.account')">
    <fio-tab-layout :tabs="accountTabs">
      <RouterView />
    </fio-tab-layout>
  </fio-page-view>
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

