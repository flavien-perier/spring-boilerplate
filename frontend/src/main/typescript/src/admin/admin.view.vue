<template>
  <section class="admin-view">
    <h1>{{ $t("menu.administration") }}</h1>
    <fio-tab-layout :tabs="adminTabs">
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

const adminTabs = computed<TabElement[]>(() => [
  {
    i18nKey: "menu.users",
    isActive: route.name === "admin-users",
    action: () => router.push({ name: "admin-users" }),
  },
]);
</script>

<style scoped lang="scss">
.admin-view {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1rem;
}
</style>
