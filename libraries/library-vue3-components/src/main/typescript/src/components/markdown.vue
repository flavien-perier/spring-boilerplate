<template>
  <div class="fio-markdown" v-html="rendered" />
</template>

<script setup lang="ts">
import { computed } from "vue";
import { markdownUtil } from "@/utils/markdown-util";

defineOptions({
  name: "FioMarkdown",
});

const props = withDefaults(
  defineProps<{
    source?: string;
    enableTitle?: boolean;
    enableLinks?: boolean;
  }>(),
  { source: "", enableTitle: true, enableLinks: true }
);

const rendered = computed(() =>
  markdownUtil.renderMarkdown(props.source, {
    enableTitle: props.enableTitle,
    enableLinks: props.enableLinks,
  })
);
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

.fio-markdown {
  color: darker(secondary, 90);
  line-height: 1.6;
  word-wrap: break-word;

  :deep(h1),
  :deep(h2),
  :deep(h3),
  :deep(h4),
  :deep(h5),
  :deep(h6) {
    margin: $margin-s 0 $margin-xs;
    font-weight: bold;
    line-height: 1.3;
  }

  :deep(h1) {
    font-size: $font-xxl-size;
  }

  :deep(h2) {
    font-size: $font-xl-size;
  }

  :deep(h3) {
    font-size: $font-l-size;
  }

  :deep(h4),
  :deep(h5) {
    font-size: $font-size;
  }

  :deep(h6) {
    font-size: $font-s-size;
  }

  :deep(p) {
    margin: $margin-xs 0;
  }

  :deep(ul),
  :deep(ol) {
    margin: $margin-xs 0;
    padding-left: $margin-xl;
  }

  :deep(li) {
    margin-bottom: $margin-xxs;
  }

  :deep(blockquote) {
    margin: $margin-xs 0;
    padding: $margin-xs $margin;
    border-left: $border-size solid lighter(secondary, 40);
    color: darker(secondary, 50);

    p {
      margin: 0;
    }
  }

  :deep(pre) {
    margin: $margin-xs 0;
    padding: $margin-s;
    background-color: lighter(secondary, 80);
    border-radius: $border-radius-size;
    overflow-x: auto;

    code {
      padding: 0;
      background: transparent;
    }
  }

  :deep(code) {
    padding: $margin-xxs $margin-xs;
    background-color: lighter(secondary, 80);
    border-radius: $border-radius-size;
    font-family: monospace;
    font-size: 0.9em;
  }

  :deep(a) {
    color: $primary;
    text-decoration: underline;

    &:hover {
      color: darker(primary, 20);
    }
  }

  :deep(strong) {
    font-weight: bold;
  }

  :deep(em) {
    font-style: italic;
  }

  :deep(del) {
    text-decoration: line-through;
  }

  :deep(hr) {
    margin: $margin 0;
    border: none;
    border-top: $border-size solid lighter(secondary, 60);
  }
}
</style>
