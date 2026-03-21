<template>
  <table class="fio-table">
    <thead>
      <tr>
        <th
          v-for="header in visibleHeaders"
          :key="header.name"
          :class="{ sortable: header.sortable }"
        >
          {{ header.name }}
          <span v-if="header.sortable" class="sort-icon">
            <fio-icon icon="sort" size="s" />
          </span>
        </th>
      </tr>
    </thead>
    <tbody>
      <slot name="body" />
    </tbody>
  </table>
</template>

<script setup lang="ts">
import { computed } from "vue";
import type { TableHeader } from "../model/table-header";
import FioIcon from "./fio-icon.vue";

defineOptions({
  name: "FioTable",
});

const props = defineProps<{
  headers: TableHeader[];
}>();

const visibleHeaders = computed(() =>
  props.headers
    .filter((h) => h.show)
    .sort((a, b) => a.position - b.position),
);
</script>
