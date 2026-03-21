<template>
  <div class="fio-tab-layout">
    <ul class="fio-tabs">
      <li v-for="tab in tabs" :key="tab.i18nKey" class="fio-tabs__item">
        <a
          class="fio-tabs__link"
          :class="{ 'fio-tabs__link--active': tab.isActive }"
          href="#"
          @click.prevent="tab.action"
        >
          {{ $t(tab.i18nKey) }}
        </a>
      </li>
    </ul>
    <div class="fio-tab-layout__content">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { TabElement } from "@/model/tab-element";

defineOptions({ name: "FioTabLayout" });

defineProps<{
  tabs: TabElement[];
}>();
</script>

<style scoped lang="scss">
@use "../styles/variables" as *;
@use "../styles/colors" as *;

.fio-tab-layout {
  &__content {
    margin-top: $margin-s;
  }
}

.fio-tabs {
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
  border-bottom: 2px solid $secondary-lighter-80;

  &__item {
    margin-bottom: -2px;
  }

  &__link {
    display: block;
    padding: $margin-xs $margin-s;
    cursor: pointer;
    text-decoration: none;
    color: $secondary;
    border-bottom: 2px solid transparent;
    transition: color 0.15s, border-color 0.15s;

    &:hover {
      color: $secondary-darker-20;
    }

    &--active {
      color: $primary;
      border-bottom-color: $primary;
      font-weight: 500;
    }
  }
}
</style>
