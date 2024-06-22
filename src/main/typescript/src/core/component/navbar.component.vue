<template>
  <nav class="navbar navbar-dark bg-dark navbar-expand-lg container-fluid mb-3">
    <button class="navbar-toggler" type="button" @click="collapse()">
      <span class="navbar-toggler-icon"></span>
    </button>

    <router-link :to="{ name: 'home' }" class="navbar-brand">
      <img
          alt="logo"
          class="d-inline-block align-top"
          height="30"
          src="../../assets/img/logo.svg"
          width="30"
      />
      demo
    </router-link>

    <div class="navbar-collapse" v-show="showNavbar">
      <ul class="navbar-nav">
        <li class="nav-item">
          <router-link :to="{ name: 'home' }" class="nav-link" @click="hide()">
            <font-awesome-icon icon="home" />
            {{ $t("home") }}
          </router-link>
        </li>
      </ul>

      <ul class="navbar-nav" v-if="applicationStore.user?.role == Role.ADMIN">
        <li class="nav-item">
          <router-link :to="{ name: 'admin-users' }" class="nav-link" @click="hide()">
            <font-awesome-icon icon="gear" />
            {{ $t("administration") }}
          </router-link>
        </li>
      </ul>

      <ul class="navbar-nav ms-auto">
        <li class="nav-item cursor-pointer" @click="changeLanguage()">
          <a class="nav-link me-5">
            <font-awesome-icon icon="flag" />
            {{ $i18n.locale }}
          </a>
        </li>
        <li class="nav-item" v-if="!isAuthenticated">
          <router-link :to="{ name: 'login' }" class="nav-link" @click="hide()">
            <font-awesome-icon icon="right-to-bracket" />
            {{ $t("login") }}
          </router-link>
        </li>
        <li class="nav-item" v-if="isAuthenticated">
          <router-link :to="{ name: 'account-information' }" class="nav-link" @click="hide()">
            <font-awesome-icon icon="user" />
            {{ $t("account") }}
          </router-link>
        </li>
        <li class="nav-item cursor-pointer" v-if="isAuthenticated">
          <a class="nav-link" @click="logout()">
            <font-awesome-icon icon="right-to-bracket" />
            {{ $t("logout") }}
          </a>
        </li>
      </ul>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useI18n } from "vue-i18n";
import {useApplicationStore} from "@/core/application.store";
import {storeToRefs} from "pinia";
import {Role} from "api-generated/api.ts";

const i18n = useI18n();

const showNavbar = ref(false);

const applicationStore = useApplicationStore();
const { isAuthenticated } = storeToRefs(applicationStore);

function collapse() {
  showNavbar.value = !showNavbar.value;
}

function hide() {
  showNavbar.value = false;
}

function logout() {
  hide();
  applicationStore.logout();
}

function changeLanguage() {
  i18n.locale.value = i18n.locale.value === "fr" ? "en" : "fr";
}
</script>
