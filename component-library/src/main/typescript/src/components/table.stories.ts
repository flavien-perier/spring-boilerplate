import type { Meta, StoryObj } from "@storybook/vue3";
import FioTable from "./table.vue";
import FioIcon from "./fio-icon.vue";
import type { TableHeader } from "../model/table-header";

const meta: Meta<typeof FioTable> = {
  title: "Components/FioTable",
  component: FioTable,
  tags: ["autodocs"],
};

export default meta;
type Story = StoryObj<typeof FioTable>;

const dateActionsHeaders: TableHeader[] = [
  { name: "Date", position: 0, sortable: false, show: true },
  { name: "Actions", position: 1, sortable: false, show: true },
];

export const Default: Story = {
  args: {
    headers: dateActionsHeaders,
  },
  render: (args) => ({
    components: { FioTable },
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
    components: { FioTable },
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
  { name: "User", position: 0, sortable: true, show: true },
  { name: "Email", position: 1, sortable: true, show: true },
  { name: "Actions", position: 2, sortable: false, show: true },
];

export const WithActions: Story = {
  args: {
    headers: userActionsHeaders,
  },
  render: (args) => ({
    components: { FioTable, FioIcon },
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
  { name: "ID", position: 0, sortable: false, show: false },
  { name: "Name", position: 1, sortable: true, show: true },
  { name: "Role", position: 2, sortable: false, show: true },
  { name: "Internal", position: 3, sortable: false, show: false },
];

export const WithHiddenColumns: Story = {
  args: {
    headers: hiddenColumnHeaders,
  },
  render: (args) => ({
    components: { FioTable },
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
