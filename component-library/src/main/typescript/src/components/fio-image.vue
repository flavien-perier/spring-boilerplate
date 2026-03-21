<template>
  <div
    class="fio-image"
    v-html="IMAGES[name]"
    :aria-label="alt || undefined"
    :role="alt ? 'img' : 'presentation'"
  />
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
.fio-image {
  :deep(svg) {
    display: block;
    max-width: 100%;
    height: auto;
  }
}
</style>
