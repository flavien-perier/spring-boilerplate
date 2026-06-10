<template>
  <div class="fio-image" v-html="IMAGES[name]" :aria-label="alt || undefined" />
</template>

<script setup lang="ts">
import { IMAGES, type ImageName } from "@/model/images";

defineOptions({ name: "FioImage" });

withDefaults(
  defineProps<{
    name: ImageName;
    alt?: string;
  }>(),
  {
    alt: "",
  }
);
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;

.fio-image {
  display: flex;
  justify-content: center;
  min-width: 100px;
  width: 100%;

  :deep(svg) {
    display: block;
    max-width: 100%;
    height: auto;
  }
}
</style>
