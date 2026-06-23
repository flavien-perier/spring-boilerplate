<template>
  <fio-page-view :title="$t('menu.administration')">
    <fio-tab-layout v-if="route.name !== 'admin-user'" :tabs="adminTabs">
      <RouterView />
    </fio-tab-layout>
    <RouterView v-else />
  </fio-page-view>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { RouterView, useRoute, useRouter } from "vue-router";
import { useApplicationStore } from "@/core/application.store";
import type { TabElement } from "@generated/component-library";

const router = useRouter();
const route = useRoute();
const applicationStore = useApplicationStore();

const adminTabs = computed<TabElement[]>(() => [
  {
    i18nKey: "menu.users",
    isActive: route.name === "admin-users" || route.name === "admin-user",
    action: () => router.push({ name: "admin-users" }),
  },
  ...(applicationStore.hasPermission("MANAGE_ALL_GROUPS")
    ? [
        {
          i18nKey: "menu.groups",
          isActive: route.name === "admin-groups",
          action: () => router.push({ name: "admin-groups" }),
        } as TabElement,
      ]
    : []),
]);
</script>
