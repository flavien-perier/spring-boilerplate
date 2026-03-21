<template>
  <section class="fio-page-view">
    <h1 class="fio-page-view__title">{{ title }}</h1>
    <slot />
  </section>
</template>

<script setup lang="ts">
defineOptions({ name: "FioPageView" });

defineProps<{
  title: string;
}>();
</script>

<style scoped lang="scss">
@use "../styles/variables" as *;

.fio-page-view {
  width: 100%;
  margin: 0 auto;
  padding: $margin;

  @media (min-width: $breakpoint-md) {
    max-width: 768px;
  }

  @media (min-width: $breakpoint-l) {
    max-width: 992px;
  }

  @media (min-width: $breakpoint-xl) {
    max-width: 1200px;
  }

  @media (min-width: $breakpoint-xxl) {
    max-width: 1400px;
  }
}
</style>
