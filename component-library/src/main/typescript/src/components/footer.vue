<template>
  <footer>
    © {{ year }} Copyright:
    <a :href="href">{{ label }}</a>
  </footer>
</template>

<script setup lang="ts">
defineOptions({
  name: "FioFooter",
});

const { href, label } = defineProps({
  href: { type: String, required: true },
  label: { type: String, required: true },
});

const year = new Date().getFullYear();
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/colors" as *;

footer {
  display: flex;
  justify-content: center;
  align-items: center;

  background-color: $secondary-darker-40;
  color: $primary-lighter-90;
  padding: $margin-s;

  a {
    padding-left: $margin-xs;
    color: $primary-lighter-90;
    text-decoration: none;
  }
}
</style>