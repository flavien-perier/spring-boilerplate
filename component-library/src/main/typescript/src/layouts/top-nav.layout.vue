<template>
  <div class="fio-top-nav-layout">
    <nav>
      <div class="nav-brand">
        <slot name="brand" />
      </div>

      <div class="nav-menu">
        <ul class="nav-left">
          <li v-for="el in startElements" :key="el.i18nKey">
            <a href="#" @click.prevent="el.action">
              <fio-icon :icon="el.faIcon" />
              {{ $t(el.i18nKey) }}
            </a>
          </li>
        </ul>
        <ul class="nav-right">
          <li v-for="el in endElements" :key="el.i18nKey">
            <a href="#" @click.prevent="el.action">
              <fio-icon :icon="el.faIcon" />
              {{ $t(el.i18nKey) }}
            </a>
          </li>
        </ul>
      </div>
    </nav>

    <main>
      <slot />
    </main>

    <footer>
      © {{ year }} Copyright:
      <a :href="footerHref">{{ footerLabel }}</a>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import type { NavbarElement } from "@/model/navbar-element";

defineOptions({ name: "FioTopNavLayout" });

const props = defineProps<{
  elements: NavbarElement[];
  footerHref: string;
  footerLabel: string;
}>();

const startElements = computed(() =>
  props.elements.filter((e) => e.direction === "start")
);
const endElements = computed(() =>
  props.elements.filter((e) => e.direction === "end")
);

const year = new Date().getFullYear();
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

.fio-top-nav-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;

  main {
    flex: 1;
    display: flex;
    flex-direction: column;
  }
}

nav {
  display: flex;
  align-items: center;
  gap: $margin-s;

  background-color: $secondary-darker-40;
  color: $primary-lighter-90;
  padding: 0 $margin;
  height: 3.5rem;

  .nav-brand {
    display: flex;
    align-items: center;
    flex-shrink: 0;

    :deep(a) {
      display: flex;
      align-items: center;
      gap: $margin-xs;
      color: $primary-lighter-90;
      text-decoration: none;
      font-weight: 600;
      font-size: $font-l-size;

      &:hover {
        color: $primary-lighter-70;
      }
    }

    :deep(img),
    :deep(.fio-image svg) {
      height: 1.75rem;
      width: auto;
    }
  }

  .nav-menu {
    display: flex;
    align-items: center;
    flex: 1;
    gap: $margin-s;

    ul {
      display: flex;
      align-items: center;
      gap: $margin-xs;
      list-style: none;
      margin: 0;
      padding: 0;
    }

    .nav-right {
      margin-left: auto;
    }

    :deep(li a) {
      display: flex;
      align-items: center;
      gap: $margin-xs;
      color: $primary-lighter-90;
      text-decoration: none;
      padding: $margin-xs $margin-s;
      border-radius: $border-radius-size;
      transition: background-color 0.2s ease, color 0.2s ease;
      white-space: nowrap;

      &:hover {
        background-color: $secondary-darker-50;
        color: $primary-lighter-70;
      }

      &.router-link-active {
        background-color: $secondary-darker-60;
      }
    }

    :deep(li) {
      cursor: pointer;
    }
  }
}

footer {
  display: flex;
  justify-content: center;
  align-items: center;

  background-color: $secondary-darker-40;
  color: $primary-lighter-90;
  padding: $margin-s;

  a {
    padding-left: $margin-xs;
    color: $primary-lighter-30;
  }
}
</style>
