<template>
  <fio-top-nav-layout
    :elements="navbarElements"
    footer-href="https://www.flavien.io/"
    footer-label="flavien.io"
  >
    <template #brand>
      <router-link :to="{ name: 'home' }">
        <fio-image name="logo" alt="logo" />
        demo
      </router-link>
    </template>

    <RouterView v-if="initOk" style="flex: auto" />
    <div v-else style="flex: auto" class="application-view">
      <fio-icon icon="spinner" size="xxl" />
    </div>
  </fio-top-nav-layout>

  <Modal />
  <Notification />
</template>

<script setup lang="ts">
import { computed } from "vue";
import { RouterView, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { storeToRefs } from "pinia";
import { useApplicationStore } from "@/core/application.store";
import { Role } from "@generated/api/api";
import Modal from "@/core/component/modal.component.vue";
import Notification from "@/core/component/notification.component.vue";
import type { NavbarElement } from "@generated/component-library";

const router = useRouter();
const i18n = useI18n();
const applicationStore = useApplicationStore();
const { initOk, isAuthenticated } = storeToRefs(applicationStore);

applicationStore.init();

const navbarElements = computed<NavbarElement[]>(() => [
  {
    i18nKey: "menu.home",
    direction: "start",
    faIcon: "home",
    action: () => router.push({ name: "home" }),
  },
  ...(applicationStore.user?.role === Role.ADMIN
    ? [
        {
          i18nKey: "menu.administration",
          direction: "start" as const,
          faIcon: "gear",
          action: () => router.push({ name: "admin-users" }),
        },
      ]
    : []),
  {
    i18nKey: "menu.language",
    direction: "end",
    faIcon: "flag",
    action: () => {
      i18n.locale.value = i18n.locale.value === "fr" ? "en" : "fr";
    },
  },
  ...(!isAuthenticated.value
    ? [
        {
          i18nKey: "menu.login",
          direction: "end" as const,
          faIcon: "right-to-bracket",
          action: () => router.push({ name: "login" }),
        },
      ]
    : []),
  ...(isAuthenticated.value
    ? [
        {
          i18nKey: "menu.account",
          direction: "end" as const,
          faIcon: "user",
          action: () => router.push({ name: "account-information" }),
        },
        {
          i18nKey: "menu.logout",
          direction: "end" as const,
          faIcon: "right-to-bracket",
          action: () => applicationStore.logout(),
        },
      ]
    : []),
]);
</script>

<style scoped lang="scss">
.application-view {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
}
</style>
