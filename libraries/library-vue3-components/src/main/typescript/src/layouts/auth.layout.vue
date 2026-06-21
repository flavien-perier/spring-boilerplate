<template>
  <fio-split-layout class="auth-layout">
    <template #left>
      <div class="auth-layout__image">
        <fio-image :name="name" />
      </div>
    </template>
    <template #right>
      <div class="auth-layout__content">
        <h1 v-if="title" class="auth-layout__title">{{ title }}</h1>
        <slot />
      </div>
    </template>
  </fio-split-layout>
</template>

<script setup lang="ts">
import FioImage from "@/components/image.vue";
import FioSplitLayout from "@/layouts/split.layout.vue";
import type { ImageName } from "@/model/images";

defineOptions({ name: "FioAuthLayout" });

defineProps<{
  name: ImageName;
  title?: string;
}>();
</script>

<style scoped lang="scss">
@use "../styles/variables" as *;

.auth-layout {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 1rem;
  width: 100%;
}

.split-layout.auth-layout {
  @media (max-width: $breakpoint-l) {
    flex-direction: column;
    gap: 0;
    align-items: stretch;
  }
}

.auth-layout__image {
  display: none;

  @media (min-width: $breakpoint-l) {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.auth-layout__content {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.auth-layout__title {
  text-align: center;
  margin-bottom: $margin-xl;
  font-size: $h1-size;
}
</style>
