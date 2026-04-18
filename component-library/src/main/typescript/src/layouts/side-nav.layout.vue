<template>
  <div class="fio-side-nav-layout" :class="{ 'is-collapsed': isCollapsed }">
    <aside class="sidebar">
      <div class="sidebar-header">
        <div v-if="!isCollapsed" class="sidebar-brand">
          <slot name="brand" />
        </div>
        <button class="sidebar-toggle" @click="isCollapsed = !isCollapsed">
          <fio-icon :icon="isCollapsed ? 'angle-right' : 'angle-left'" />
        </button>
      </div>

      <nav>
        <ul>
          <li v-for="el in startElements" :key="el.i18nKey">
            <a href="#" @click.prevent="el.action">
              <fio-icon :icon="el.faIcon" />
              <span v-if="!isCollapsed" class="nav-label">
                {{ $t(el.i18nKey) }}
              </span>
            </a>
          </li>
        </ul>
        <ul class="nav-end">
          <li v-for="el in endElements" :key="el.i18nKey">
            <a href="#" @click.prevent="el.action">
              <fio-icon :icon="el.faIcon" />
              <span v-if="!isCollapsed" class="nav-label">
                {{ $t(el.i18nKey) }}
              </span>
            </a>
          </li>
        </ul>
      </nav>
    </aside>

    <div class="main-content">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import type { NavbarElement } from "@/model/navbar-element";

defineOptions({ name: "FioSideNavLayout" });

const props = defineProps<{
  elements: NavbarElement[];
}>();

const isCollapsed = ref(false);

const startElements = computed(() =>
  props.elements.filter((el) => el.direction === "start")
);
const endElements = computed(() =>
  props.elements.filter((el) => el.direction === "end")
);
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

.fio-side-nav-layout {
  display: flex;
  min-height: 100vh;

  .sidebar {
    display: flex;
    flex-direction: column;
    width: 240px;
    min-height: 100vh;
    background-color: $secondary-darker-40;
    color: $primary-lighter-90;
    transition: width 0.2s ease;
    flex-shrink: 0;

    .sidebar-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: $margin-s $margin;
      height: 3.5rem;

      .sidebar-brand {
        flex: 1;
        overflow: hidden;
        white-space: nowrap;

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

          img {
            height: 1.75rem;
            width: auto;
          }
        }
      }

      .sidebar-toggle {
        display: flex;
        align-items: center;
        justify-content: center;
        background: none;
        border: none;
        color: $primary-lighter-90;
        cursor: pointer;
        padding: $margin-xs;
        border-radius: $border-radius-size;
        transition: background-color 0.2s ease;
        flex-shrink: 0;

        &:hover {
          background-color: $secondary-darker-50;
        }
      }
    }

    nav {
      flex: 1;
      display: flex;
      flex-direction: column;

      .nav-end {
        margin-top: auto;
      }
    }

    nav ul {
      list-style: none;
      margin: 0;
      padding: $margin-s 0;

      li {
        cursor: pointer;

        a {
          display: flex;
          align-items: center;
          gap: $margin-s;
          color: $primary-lighter-90;
          text-decoration: none;
          padding: $margin-s $margin;
          transition: background-color 0.2s ease, color 0.2s ease;
          white-space: nowrap;

          &:hover {
            background-color: $secondary-darker-50;
            color: $primary-lighter-70;
          }
        }
      }
    }
  }

  .main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: auto;
  }

  &.is-collapsed .sidebar {
    width: 60px;

    .sidebar-header {
      justify-content: center;
      padding: $margin-s;
    }

    nav ul li a {
      justify-content: center;
      padding: $margin-s;
    }
  }
}
</style>
