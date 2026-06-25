import type { Meta, StoryObj } from "@storybook/vue3";
import FioTable from "./table.vue";
import type { TableHeader, SortDirection } from "../model/table-header";

const meta: Meta<typeof FioTable> = {
  title: "Components/FioTable",
  component: FioTable,
  tags: ["autodocs"],
};

export default meta;
type Story = StoryObj<typeof FioTable>;

const dateActionsHeaders: TableHeader[] = [
  { key: "date", name: "Date", position: 0, show: true },
  { key: "actions", name: "Actions", position: 1, show: true },
];

export const Default: Story = {
  args: {
    headers: dateActionsHeaders,
  },
  render: (args) => ({
    setup() {
      return { args };
    },
    template: `
      <fio-table v-bind="args">
        <template #body>
          <tr>
            <td>2026-01-15</td>
            <td>Edit</td>
          </tr>
          <tr>
            <td>2026-02-20</td>
            <td>Edit</td>
          </tr>
        </template>
      </fio-table>
    `,
  }),
};

export const EmptyBody: Story = {
  args: {
    headers: dateActionsHeaders,
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

const userActionsHeaders: TableHeader[] = [
  { key: "user", name: "User", position: 0, sortable: true, show: true },
  { key: "email", name: "Email", position: 1, sortable: true, show: true },
  { key: "actions", name: "Actions", position: 2, show: true },
];

export const WithActions: Story = {
  args: {
    headers: userActionsHeaders,
  },
  render: (args) => ({
    setup() {
      return { args };
    },
    template: `
      <fio-table v-bind="args">
        <template #body>
          <tr>
            <td>Alice</td>
            <td>alice@example.com</td>
            <td>
              <a href="#" class="text-danger">
                Delete <fio-icon icon="trash" />
              </a>
            </td>
          </tr>
          <tr>
            <td>Bob</td>
            <td>bob@example.com</td>
            <td>
              <a href="#" class="text-danger">
                Delete <fio-icon icon="trash" />
              </a>
            </td>
          </tr>
        </template>
      </fio-table>
    `,
  }),
};

const hiddenColumnHeaders: TableHeader[] = [
  { key: "id", name: "ID", position: 0, show: false },
  { key: "name", name: "Name", position: 1, sortable: true, show: true },
  { key: "role", name: "Role", position: 2, show: true },
  { key: "internal", name: "Internal", position: 3, show: false },
];

export const WithHiddenColumns: Story = {
  args: {
    headers: hiddenColumnHeaders,
  },
  render: (args) => ({
    setup() {
      return { args };
    },
    template: `
      <fio-table v-bind="args">
        <template #body>
          <tr>
            <td>Alice</td>
            <td>Admin</td>
          </tr>
          <tr>
            <td>Bob</td>
            <td>User</td>
          </tr>
        </template>
      </fio-table>
    `,
  }),
};

const sortableHeaders: TableHeader[] = [
  { key: "user", name: "User", position: 0, sortable: true, show: true },
  { key: "email", name: "Email", position: 1, sortable: true, show: true },
  { key: "role", name: "Role", position: 2, sortable: true, show: true },
];

export const WithSorting: Story = {
  args: {
    headers: sortableHeaders,
    sortKey: "user",
    sortDirection: "asc" as SortDirection,
  },
  render: (args) => ({
    setup() {
      return { args };
    },
    template: `
      <fio-table v-bind="args" @sort-change="(e) => console.log('sort', e)">
        <template #body>
          <tr>
            <td>Alice</td>
            <td>alice@example.com</td>
            <td>Admin</td>
          </tr>
          <tr>
            <td>Bob</td>
            <td>bob@example.com</td>
            <td>User</td>
          </tr>
        </template>
      </fio-table>
    `,
  }),
};

export const WithPagination: Story = {
  args: {
    headers: userActionsHeaders,
    currentPage: 2,
    pageSize: 10,
    totalDataCount: 42,
  },
  render: (args) => ({
    setup() {
      return { args };
    },
    template: `
      <fio-table v-bind="args"
        @page-change="(p) => console.log('page', p)"
        @page-size-change="(s) => console.log('pageSize', s)">
        <template #body>
          <tr>
            <td>Alice</td>
            <td>alice@example.com</td>
            <td>
              <a href="#" class="text-danger">
                Delete <fio-icon icon="trash" />
              </a>
            </td>
          </tr>
        </template>
      </fio-table>
    `,
  }),
};

const i18nHeaders: TableHeader[] = [
  { key: "user", i18nKey: "fio.table.example.user", position: 0, sortable: true, show: true },
  { key: "email", i18nKey: "fio.table.example.email", position: 1, sortable: true, show: true },
];

export const WithI18nHeaders: Story = {
  args: {
    headers: i18nHeaders,
  },
  render: (args) => ({
    setup() {
      return { args };
    },
    template: `
      <fio-table v-bind="args">
        <template #body>
          <tr>
            <td>Alice</td>
            <td>alice@example.com</td>
          </tr>
        </template>
      </fio-table>
    `,
  }),
};
