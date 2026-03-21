<template>
  <fio-navbar>
    <template #nav-brand>
      <router-link :to="{ name: 'home' }">
        <img
            alt="logo"
            height="30"
            src="../../assets/img/logo.svg"
            width="30"
        />
        demo
      </router-link>
    </template>

    <template #nav-left>
      <li>
        <router-link :to="{ name: 'home' }">
          <fio-icon icon="home" />
          {{ $t("menu.home") }}
        </router-link>
      </li>

      <li v-if="applicationStore.user?.role == Role.ADMIN">
        <router-link :to="{ name: 'admin-users' }">
          <fio-icon icon="gear" />
          {{ $t("menu.administration") }}
        </router-link>
      </li>
    </template>

    <template #nav-right>
      <li @click="changeLanguage()">
        <a>
          <fio-icon icon="flag" />
          {{ $i18n.locale }}
        </a>
      </li>

      <li v-if="!isAuthenticated">
        <router-link :to="{ name: 'login' }">
          <fio-icon icon="right-to-bracket" />
          {{ $t("menu.login") }}
        </router-link>
      </li>

      <li v-if="isAuthenticated">
        <router-link :to="{ name: 'account-information' }">
          <fio-icon icon="user" />
          {{ $t("menu.account") }}
        </router-link>
      </li>

      <li v-if="isAuthenticated" @click="logout()">
        <a>
          <fio-icon icon="right-to-bracket" />
          {{ $t("menu.logout") }}
        </a>
      </li>
    </template>
  </fio-navbar>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import { useApplicationStore } from "@/core/application.store";
import { storeToRefs } from "pinia";
import { Role } from "@generated/api/api";

const i18n = useI18n();
const applicationStore = useApplicationStore();
const { isAuthenticated } = storeToRefs(applicationStore);

function logout() {
  applicationStore.logout();
}

function changeLanguage() {
  i18n.locale.value = i18n.locale.value === "fr" ? "en" : "fr";
}
</script>
