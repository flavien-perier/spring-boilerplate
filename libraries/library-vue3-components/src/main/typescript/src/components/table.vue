<template>
  <div class="fio-table-wrapper">
    <table
      class="fio-table"
      :class="{ 'fio-table--with-footer': showPagination }"
    >
      <thead>
        <tr>
          <th
            v-for="header in visibleHeaders"
            :key="header.key ?? header.i18nKey ?? header.name"
            :class="{
              'fio-table__th--sortable': header.sortable,
              'fio-table__th--active':
                header.sortable && header.key === sortKey && sortDirection,
              'fio-table__th--asc':
                header.sortable &&
                header.key === sortKey &&
                sortDirection === 'asc',
              'fio-table__th--desc':
                header.sortable &&
                header.key === sortKey &&
                sortDirection === 'desc',
            }"
            @click="header.sortable ? onSortClick(header) : null"
          >
            <span class="fio-table__th-label">
              {{ resolveLabel(header) }}
            </span>
            <span v-if="header.sortable" class="fio-table__sort-icon">
              <fio-icon
                :icon="sortIcon(header)"
                size="s"
                :color="
                  header.key === sortKey && sortDirection
                    ? 'primary'
                    : undefined
                "
              />
            </span>
          </th>
        </tr>
      </thead>
      <tbody>
        <slot name="body" />
      </tbody>
    </table>

    <div v-if="showPagination" class="fio-table__footer">
      <fio-input-select
        class="fio-table__page-size"
        size="xs"
        :model-value="String(pageSize)"
        :options="pageSizeSelectOptions"
        @update:model-value="onPageSizeChange"
      />

      <div class="fio-table__pager">
        <fio-input-button
          v-for="btn in pagerButtonsBefore"
          :key="btn.key"
          class="fio-table__pager-btn"
          variant="ghost"
          size="xs"
          :icon="btn.icon"
          :disabled="btn.disabled"
          @click="btn.go()"
        />

        <span class="fio-table__pager-current"
          >{{ currentPage }} / {{ totalPages }}</span
        >

        <fio-input-button
          v-for="btn in pagerButtonsAfter"
          :key="btn.key"
          class="fio-table__pager-btn"
          variant="ghost"
          size="xs"
          :icon="btn.icon"
          :disabled="btn.disabled"
          @click="btn.go()"
        />
      </div>

      <span class="fio-table__footer-spacer" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import type { SortDirection, TableHeader } from "../model/table-header";
import FioIcon from "./icon.vue";
import FioInputSelect from "./inputs/input-select.vue";
import type { SelectOption } from "./inputs/input-select.vue";
import FioInputButton from "./inputs/input-button.vue";

defineOptions({
  name: "FioTable",
});

const props = withDefaults(
  defineProps<{
    headers: TableHeader[];
    currentPage?: number;
    pageSize?: number;
    pageSizeOptions?: number[];
    totalDataCount?: number;
    totalPages?: number;
    sortKey?: string | null;
    sortDirection?: SortDirection;
  }>(),
  {
    currentPage: 1,
    pageSize: 10,
    pageSizeOptions: () => [10, 25, 50, 100],
    totalDataCount: undefined,
    totalPages: undefined,
    sortKey: null,
    sortDirection: null,
  }
);

const emit = defineEmits<{
  pageChange: [page: number];
  pageSizeChange: [pageSize: number];
  sortChange: [payload: { key: string; direction: SortDirection }];
}>();

const { t } = useI18n();

const visibleHeaders = computed(() =>
  props.headers
    .filter((h) => h.show !== false)
    .sort((a, b) => a.position - b.position)
);

function resolveLabel(header: TableHeader): string {
  if (header.i18nKey) return t(header.i18nKey);
  return header.name ?? "";
}

const showPagination = computed(
  () => props.totalDataCount !== undefined || props.totalPages !== undefined
);

const totalPages = computed(() => {
  if (props.totalPages !== undefined) return props.totalPages;
  if (props.totalDataCount !== undefined) {
    return Math.max(1, Math.ceil(props.totalDataCount / props.pageSize));
  }
  return 1;
});

const pageSizeSelectOptions = computed<SelectOption[]>(() =>
  props.pageSizeOptions.map((size) => ({
    value: String(size),
    label: String(size),
  }))
);

interface PagerButton {
  key: string;
  icon: string;
  disabled: boolean;
  go: () => void;
}

const pagerButtonsBefore = computed<PagerButton[]>(() => [
  {
    key: "first",
    icon: "angles-left",
    disabled: props.currentPage <= 1,
    go: () => goToPage(1),
  },
  {
    key: "prev",
    icon: "angle-left",
    disabled: props.currentPage <= 1,
    go: () => goToPage(props.currentPage - 1),
  },
]);

const pagerButtonsAfter = computed<PagerButton[]>(() => [
  {
    key: "next",
    icon: "angle-right",
    disabled: props.currentPage >= totalPages.value,
    go: () => goToPage(props.currentPage + 1),
  },
  {
    key: "last",
    icon: "angles-right",
    disabled: props.currentPage >= totalPages.value,
    go: () => goToPage(totalPages.value),
  },
]);

function goToPage(page: number) {
  const clamped = Math.min(Math.max(1, page), totalPages.value);
  if (clamped !== props.currentPage) {
    emit("pageChange", clamped);
  }
}

function onPageSizeChange(value: string | null) {
  if (value !== null) emit("pageSizeChange", Number(value));
}

function sortIcon(header: TableHeader): string {
  if (header.key !== props.sortKey || !props.sortDirection) return "sort";
  return props.sortDirection === "asc" ? "sort-up" : "sort-down";
}

function onSortClick(header: TableHeader) {
  if (!header.key) return;
  let nextDirection: SortDirection;
  if (header.key === props.sortKey) {
    if (props.sortDirection === "asc") nextDirection = "desc";
    else if (props.sortDirection === "desc") nextDirection = null;
    else nextDirection = "asc";
  } else {
    nextDirection = "asc";
  }
  emit("sortChange", { key: header.key, direction: nextDirection });
}
</script>
