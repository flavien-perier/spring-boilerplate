<template>
  <nav>
    <div class="nav-brand">
      <slot name="nav-brand" />
    </div>

    <div class="nav-menu">
      <ul class="nav-left">
        <slot name="nav-left" />
      </ul>
      <ul class="nav-right">
        <slot name="nav-right" />
      </ul>
    </div>
  </nav>
</template>

<script setup lang="ts">
defineOptions({
  name: "FioNavbar",
});
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/colors" as *;

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

    :deep(img) {
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
      border-radius: 4px;
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
</style>
