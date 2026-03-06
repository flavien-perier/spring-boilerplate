<template>
  <section class="container">
    <h1>{{ $t("administration") }}</h1>
    <ul class="nav nav-tabs mt-3 mb-2">
      <li class="nav-item">
        <router-link
            :to="{ name: 'admin-users' }"
            class="nav-link"
            :class="{ active: $route.name == 'admin-users' }"
        >
          {{ $t("users") }}
        </router-link>
      </li>
    </ul>

    <RouterView />
  </section>
</template>

<script setup lang="ts">
import {RouterView} from "vue-router";
</script>