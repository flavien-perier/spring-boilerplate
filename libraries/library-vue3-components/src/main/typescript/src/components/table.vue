<template>
  <div class="fio-table-wrapper">
    <table class="fio-table">
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
      <div class="fio-table__page-size">
        <label for="fio-table-page-size" class="fio-table__page-size-label">
          {{ $t("fio.table.page-size") }}
        </label>
        <select
          id="fio-table-page-size"
          class="fio-table__page-size-select"
          :value="pageSize"
          @change="onPageSizeChange"
        >
          <option
            v-for="size in pageSizeOptions"
            :key="size"
            :value="size"
            :selected="size === pageSize"
          >
            {{ size }}
          </option>
        </select>
      </div>

      <div class="fio-table__pager">
        <span class="fio-table__pager-info">
          {{
            $t("fio.table.page-info", { page: currentPage, total: totalPages })
          }}
        </span>

        <button
          type="button"
          class="fio-table__pager-btn"
          :disabled="currentPage <= 1"
          @click="goToPage(1)"
        >
          <fio-icon icon="angle-left" size="s" />
          <fio-icon icon="angle-left" size="s" />
        </button>
        <button
          type="button"
          class="fio-table__pager-btn"
          :disabled="currentPage <= 1"
          @click="goToPage(currentPage - 1)"
        >
          <fio-icon icon="angle-left" size="s" />
        </button>

        <span class="fio-table__pager-current">{{ currentPage }}</span>

        <button
          type="button"
          class="fio-table__pager-btn"
          :disabled="currentPage >= totalPages"
          @click="goToPage(currentPage + 1)"
        >
          <fio-icon icon="angle-right" size="s" />
        </button>
        <button
          type="button"
          class="fio-table__pager-btn"
          :disabled="currentPage >= totalPages"
          @click="goToPage(totalPages)"
        >
          <fio-icon icon="angle-right" size="s" />
          <fio-icon icon="angle-right" size="s" />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import type { SortDirection, TableHeader } from "../model/table-header";
import FioIcon from "./icon.vue";

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

function goToPage(page: number) {
  const clamped = Math.min(Math.max(1, page), totalPages.value);
  if (clamped !== props.currentPage) {
    emit("pageChange", clamped);
  }
}

function onPageSizeChange(event: Event) {
  const target = event.target as HTMLSelectElement;
  emit("pageSizeChange", Number(target.value));
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
