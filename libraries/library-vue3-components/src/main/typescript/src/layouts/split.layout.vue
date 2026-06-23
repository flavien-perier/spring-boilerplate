<template>
  <div class="split-layout" :class="`split-layout--${align}`">
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

withDefaults(
  defineProps<{
    align?: "center" | "start" | "stretch";
  }>(),
  {
    align: "center",
  }
);
</script>

<style scoped lang="scss">
@use "../styles/variables" as *;

.split-layout {
  display: flex;
  flex-direction: column;
  width: 100%;

  @media (min-width: $breakpoint-md) {
    flex-direction: row;
    gap: $margin-xl;
  }

  &--center {
    @media (min-width: $breakpoint-md) {
      align-items: center;
    }
  }

  &--start {
    @media (min-width: $breakpoint-md) {
      align-items: flex-start;
    }
  }

  &--stretch {
    @media (min-width: $breakpoint-md) {
      align-items: stretch;
    }
  }

  &__left,
  &__right {
    flex: 1;
  }
}
</style>
