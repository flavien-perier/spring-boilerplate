<template>
  <fio-page-view :title="$t('menu.administration')">
    <fio-tab-layout :tabs="adminTabs">
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

const adminTabs = computed<TabElement[]>(() => [
  {
    i18nKey: "menu.users",
    isActive: route.name === "admin-users",
    action: () => router.push({ name: "admin-users" }),
  },
]);
</script>

