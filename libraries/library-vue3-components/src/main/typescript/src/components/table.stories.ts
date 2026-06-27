import type { Meta, StoryObj } from "@storybook/vue3";
import { computed, ref } from "vue";
import { expect, userEvent, within } from "storybook/test";
import FioTable from "./table.vue";
import type { SortDirection, TableHeader } from "../model/table-header";

const meta: Meta<typeof FioTable> = {
  title: "Components/FioTable",
  component: FioTable,
  tags: ["autodocs"],
};

export default meta;
type Story = StoryObj<typeof FioTable>;

/* ------------------------------------------------------------------ *
 * Demo dataset + simulation helper                                    *
 *                                                                     *
 * `fio-table` is a controlled component: it only emits sort/page      *
 * events, the parent owns the data (exactly like the admin panel,     *
 * where a Pinia store does the work). These stories simulate that     *
 * store locally so sorting / paging / filtering actually react.       *
 * ------------------------------------------------------------------ */

interface DemoRow {
  id: number;
  name: string;
  category: string;
  value: number;
  date: string;
}

const FIRST_NAMES = [
  "Alice",
  "Bob",
  "Carol",
  "David",
  "Eve",
  "Frank",
  "Grace",
  "Heidi",
  "Ivan",
  "Judy",
  "Mallory",
  "Niaj",
  "Olivia",
  "Peggy",
  "Rupert",
  "Sybil",
  "Trent",
  "Victor",
  "Wendy",
  "Xavier",
];
const LAST_NAMES = [
  "Martin",
  "Bernard",
  "Dubois",
  "Thomas",
  "Robert",
  "Petit",
  "Durand",
  "Leroy",
  "Moreau",
  "Simon",
];
const CATEGORIES = [
  "Electronics",
  "Books",
  "Clothing",
  "Home",
  "Toys",
  "Sports",
];

function buildDataset(count = 54): DemoRow[] {
  const baseDate = Date.UTC(2025, 0, 1);
  return Array.from({ length: count }, (_, i) => {
    const first = FIRST_NAMES[i % FIRST_NAMES.length];
    const last =
      LAST_NAMES[(i + Math.floor(i / FIRST_NAMES.length)) % LAST_NAMES.length];
    return {
      id: i + 1,
      name: `${first} ${last}`,
      category: CATEGORIES[i % CATEGORIES.length],
      value: (i * 37 + 13) % 1000,
      date: new Date(baseDate + i * 86_400_000).toISOString().slice(0, 10),
    };
  });
}

const dataset = buildDataset();

function compare(a: DemoRow, b: DemoRow, key: string): number {
  const av = a[key as keyof DemoRow];
  const bv = b[key as keyof DemoRow];
  if (typeof av === "number" && typeof bv === "number") return av - bv;
  return String(av).localeCompare(String(bv));
}

function createTableState(
  source: DemoRow[],
  opts: {
    pageSize?: number;
    sortKey?: string | null;
    sortDirection?: SortDirection;
  } = {}
) {
  const rows = ref<DemoRow[]>([...source]);
  const search = ref("");
  const sortKey = ref<string | null>(opts.sortKey ?? null);
  const sortDirection = ref<SortDirection>(opts.sortDirection ?? null);
  const currentPage = ref(1);
  const pageSize = ref(opts.pageSize ?? 10);

  const filtered = computed(() => {
    const q = search.value.trim().toLowerCase();
    if (!q) return rows.value;
    return rows.value.filter(
      (r) =>
        r.name.toLowerCase().includes(q) || r.category.toLowerCase().includes(q)
    );
  });

  const sorted = computed(() => {
    const key = sortKey.value;
    const dir = sortDirection.value;
    if (!key || !dir) return filtered.value;
    const factor = dir === "asc" ? 1 : -1;
    return [...filtered.value].sort((a, b) => factor * compare(a, b, key));
  });

  const total = computed(() => sorted.value.length);

  const pageRows = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value;
    return sorted.value.slice(start, start + pageSize.value);
  });

  function onSortChange(payload: { key: string; direction: SortDirection }) {
    sortKey.value = payload.key;
    sortDirection.value = payload.direction;
    currentPage.value = 1;
  }
  function onPageChange(page: number) {
    currentPage.value = page;
  }
  function onPageSizeChange(size: number) {
    pageSize.value = size;
    currentPage.value = 1;
  }
  function onSearch() {
    currentPage.value = 1;
  }
  function onDelete(id: number) {
    rows.value = rows.value.filter((r) => r.id !== id);
  }

  return {
    search,
    sortKey,
    sortDirection,
    currentPage,
    pageSize,
    total,
    pageRows,
    onSortChange,
    onPageChange,
    onPageSizeChange,
    onSearch,
    onDelete,
  };
}

const fullHeaders: TableHeader[] = [
  { key: "id", name: "ID", position: 0, sortable: true, show: true },
  { key: "name", name: "Name", position: 1, sortable: true, show: true },
  {
    key: "category",
    name: "Category",
    position: 2,
    sortable: true,
    show: true,
  },
  { key: "value", name: "Value", position: 3, sortable: true, show: true },
  { key: "date", name: "Date", position: 4, sortable: true, show: true },
];

const sortableHeaders: TableHeader[] = [
  { key: "name", name: "Name", position: 0, sortable: true, show: true },
  {
    key: "category",
    name: "Category",
    position: 1,
    sortable: true,
    show: true,
  },
  { key: "value", name: "Value", position: 2, sortable: true, show: true },
  { key: "date", name: "Date", position: 3, sortable: true, show: true },
];

/* ------------------------------------------------------------------ */

export const Default: Story = {
  args: {
    headers: fullHeaders,
  },
  render: (args) => ({
    setup() {
      const rows = dataset.slice(0, 5);
      return { args, rows };
    },
    template: `
      <fio-table v-bind="args">
        <template #body>
          <tr v-for="row in rows" :key="row.id">
            <td>{{ row.id }}</td>
            <td>{{ row.name }}</td>
            <td>{{ row.category }}</td>
            <td>{{ row.value }}</td>
            <td>{{ row.date }}</td>
          </tr>
        </template>
      </fio-table>
    `,
  }),
};

export const EmptyBody: Story = {
  args: {
    headers: fullHeaders,
  },
  render: (args) => ({
    setup() {
      return { args };
    },
    template: `
      <fio-table v-bind="args">
        <template #body>
          <tr>
            <td :colspan="args.headers.length" style="text-align: center; color: #6c757d;">No data</td>
          </tr>
        </template>
      </fio-table>
    `,
  }),
};

const actionsHeaders: TableHeader[] = [
  { key: "name", name: "Name", position: 0, sortable: true, show: true },
  {
    key: "category",
    name: "Category",
    position: 1,
    sortable: true,
    show: true,
  },
  { key: "value", name: "Value", position: 2, sortable: true, show: true },
  { key: "actions", name: "Actions", position: 3, show: true },
];

export const WithActions: Story = {
  args: {
    headers: actionsHeaders,
  },
  render: (args) => ({
    setup() {
      const state = createTableState(dataset.slice(0, 6));
      return { args, ...state };
    },
    template: `
      <fio-table
        v-bind="args"
        :sort-key="sortKey"
        :sort-direction="sortDirection"
        @sort-change="onSortChange"
      >
        <template #body>
          <tr v-for="row in pageRows" :key="row.id">
            <td>{{ row.name }}</td>
            <td>{{ row.category }}</td>
            <td>{{ row.value }}</td>
            <td>
              <a href="#" class="text-danger" @click.prevent="onDelete(row.id)">
                Delete <fio-icon icon="trash" />
              </a>
            </td>
          </tr>
          <tr v-if="pageRows.length === 0">
            <td :colspan="args.headers.length" style="text-align: center; color: #6c757d;">No data</td>
          </tr>
        </template>
      </fio-table>
    `,
  }),
};

const hiddenColumnHeaders: TableHeader[] = [
  { key: "id", name: "ID", position: 0, show: false },
  { key: "name", name: "Name", position: 1, sortable: true, show: true },
  { key: "category", name: "Category", position: 2, show: true },
  { key: "internal", name: "Internal", position: 3, show: false },
];

export const WithHiddenColumns: Story = {
  args: {
    headers: hiddenColumnHeaders,
  },
  render: (args) => ({
    setup() {
      const rows = dataset.slice(0, 5);
      return { args, rows };
    },
    template: `
      <fio-table v-bind="args">
        <template #body>
          <tr v-for="row in rows" :key="row.id">
            <td>{{ row.name }}</td>
            <td>{{ row.category }}</td>
          </tr>
        </template>
      </fio-table>
    `,
  }),
};

export const WithSorting: Story = {
  args: {
    headers: sortableHeaders,
  },
  render: (args) => ({
    setup() {
      const state = createTableState(dataset, { pageSize: 8 });
      return { args, ...state };
    },
    template: `
      <fio-table
        v-bind="args"
        :sort-key="sortKey"
        :sort-direction="sortDirection"
        @sort-change="onSortChange"
      >
        <template #body>
          <tr v-for="row in pageRows" :key="row.id">
            <td>{{ row.name }}</td>
            <td>{{ row.category }}</td>
            <td>{{ row.value }}</td>
            <td>{{ row.date }}</td>
          </tr>
        </template>
      </fio-table>
    `,
  }),
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement);

    // Click the "Name" header → rows re-sort ascending by name.
    await userEvent.click(canvas.getByText("Name"));

    const nameCells = Array.from(
      canvasElement.querySelectorAll<HTMLTableCellElement>(
        "tbody tr td:first-child"
      )
    ).map((td) => td.textContent?.trim() ?? "");

    expect(nameCells.length).toBeGreaterThan(0);
    const ascending = [...nameCells].sort((a, b) => a.localeCompare(b));
    expect(nameCells).toEqual(ascending);
  },
};

export const WithPagination: Story = {
  args: {
    headers: fullHeaders,
  },
  render: (args) => ({
    setup() {
      const state = createTableState(dataset, { pageSize: 10 });
      return { args, ...state };
    },
    template: `
      <fio-table
        v-bind="args"
        :sort-key="sortKey"
        :sort-direction="sortDirection"
        :current-page="currentPage"
        :page-size="pageSize"
        :total-data-count="total"
        @sort-change="onSortChange"
        @page-change="onPageChange"
        @page-size-change="onPageSizeChange"
      >
        <template #body>
          <tr v-for="row in pageRows" :key="row.id">
            <td>{{ row.id }}</td>
            <td>{{ row.name }}</td>
            <td>{{ row.category }}</td>
            <td>{{ row.value }}</td>
            <td>{{ row.date }}</td>
          </tr>
        </template>
      </fio-table>
    `,
  }),
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement);

    // Page 1 starts at id 1.
    expect(
      canvasElement.querySelector("tbody tr td")?.textContent?.trim()
    ).toBe("1");

    // Pager buttons are icon-only: [first, prev, next, last].
    const pagerButtons = canvasElement.querySelectorAll<HTMLButtonElement>(
      ".fio-table__pager-btn"
    );
    await userEvent.click(pagerButtons[2]); // next page

    expect(
      canvasElement
        .querySelector(".fio-table__pager-current")
        ?.textContent?.trim()
    ).toBe("2");
    expect(
      canvasElement.querySelector("tbody tr td")?.textContent?.trim()
    ).toBe("11");

    // Changing the page size resets to page 1 and shows 25 rows.
    const sizeSelect = canvas.getByLabelText(/page size/i);
    await userEvent.selectOptions(sizeSelect, "25");
    expect(canvasElement.querySelectorAll("tbody tr").length).toBe(25);
  },
};

const i18nHeaders: TableHeader[] = [
  {
    key: "name",
    i18nKey: "fio.table.example.user",
    position: 0,
    sortable: true,
    show: true,
  },
  {
    key: "email",
    i18nKey: "fio.table.example.email",
    position: 1,
    sortable: true,
    show: true,
  },
];

export const WithI18nHeaders: Story = {
  args: {
    headers: i18nHeaders,
  },
  render: (args) => ({
    setup() {
      const rows = dataset.slice(0, 4);
      return { args, rows };
    },
    template: `
      <fio-table v-bind="args">
        <template #body>
          <tr v-for="row in rows" :key="row.id">
            <td>{{ row.name }}</td>
            <td>{{ row.name.toLowerCase().replace(' ', '.') }}@example.com</td>
          </tr>
        </template>
      </fio-table>
    `,
  }),
};

export const InteractivePlayground: Story = {
  name: "Interactive Playground",
  args: {
    headers: sortableHeaders,
  },
  render: (args) => ({
    setup() {
      const state = createTableState(dataset, { pageSize: 10 });
      return { args, ...state };
    },
    template: `
      <div style="width: 720px; max-width: 100%;">
        <div style="margin-bottom: 0.5rem;">
          <fio-input-text
            v-model="search"
            placeholder="Search by name or category..."
            @input="onSearch"
          />
        </div>
        <fio-table
          v-bind="args"
          :sort-key="sortKey"
          :sort-direction="sortDirection"
          :current-page="currentPage"
          :page-size="pageSize"
          :total-data-count="total"
          @sort-change="onSortChange"
          @page-change="onPageChange"
          @page-size-change="onPageSizeChange"
        >
          <template #body>
            <tr v-for="row in pageRows" :key="row.id">
              <td>{{ row.name }}</td>
              <td>{{ row.category }}</td>
              <td>{{ row.value }}</td>
              <td>{{ row.date }}</td>
            </tr>
            <tr v-if="pageRows.length === 0">
              <td :colspan="args.headers.length" style="text-align: center; color: #6c757d;">No results</td>
            </tr>
          </template>
        </fio-table>
      </div>
    `,
  }),
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement);

    // Filtering by a category narrows the table to matching rows only.
    const searchInput = canvas.getByPlaceholderText(/search by name/i);
    await userEvent.type(searchInput, "Books");

    const categoryCells = Array.from(
      canvasElement.querySelectorAll<HTMLTableCellElement>(
        "tbody tr td:nth-child(2)"
      )
    ).map((td) => td.textContent?.trim());

    expect(categoryCells.length).toBeGreaterThan(0);
    expect(categoryCells.every((c) => c === "Books")).toBe(true);
  },
};
