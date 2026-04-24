<template>
  <div class="split-layout">
    <div class="split-layout__left">
      <slot name="left" />
    </div>
    <div class="split-layout__right">
      <slot name="right" />
    </div>
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: "FioSplitLayout" });
</script>

<style scoped lang="scss">
@use "../styles/variables" as *;

.split-layout {
  display: flex;
  flex-direction: column;
  width: 100%;

  @media (min-width: $breakpoint-md) {
    flex-direction: row;
    gap: 2rem;
    align-items: center;
  }

  &__left,
  &__right {
    flex: 1;
  }
}
</style>
