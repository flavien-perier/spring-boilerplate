<template>
  <div class="auth-layout">
    <div class="auth-layout__image">
      <fio-image :name="name" />
    </div>
    <div class="auth-layout__content">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import FioImage from "@/components/fio-image.vue";
import type { ImageName } from "@/model/images";

defineOptions({ name: "FioAuthLayout" });

defineProps<{
  name: ImageName;
}>();
</script>

<style scoped lang="scss">
@use "../styles/variables" as *;

.auth-layout {
  display: grid;
  grid-template-columns: 1fr;
  width: 100%;
  align-items: center;
  justify-content: center;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 1rem;

  @media (min-width: $breakpoint-md) {
    grid-template-columns: 8fr 7fr;
    gap: 2rem;
  }

  @media (min-width: $breakpoint-l) {
    grid-template-columns: 7fr 5fr;
  }

  @media (min-width: $breakpoint-xl) {
    grid-template-columns: 6fr 5fr;
  }
}

.auth-layout__image {
  display: none;

  @media (min-width: $breakpoint-md) {
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
</style>
