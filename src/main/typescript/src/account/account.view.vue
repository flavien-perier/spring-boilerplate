<template>
  <section class="container">
    <h1>{{ $t("account") }}</h1>
    <ul class="nav nav-tabs mt-3 mb-2">
      <li class="nav-item">
        <router-link
            :to="{ name: 'account-information' }"
            class="nav-link"
            :class="{ active: $route.name == 'account-information' }"
        >
          {{ $t("information") }}
        </router-link>
      </li>
      <li class="nav-item">
        <router-link
            :to="{ name: 'account-security' }"
            class="nav-link"
            :class="{ active: $route.name == 'account-security' }"
        >
          {{ $t("security") }}
        </router-link>
      </li>
    </ul>

    <RouterView />
  </section>
</template>

<script setup lang="ts">
import {RouterView} from "vue-router";
</script>
