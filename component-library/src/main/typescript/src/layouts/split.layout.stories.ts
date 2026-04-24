import type { Meta, StoryObj } from "@storybook/vue3";
import FioSplitLayout from "./split.layout.vue";

const meta: Meta<typeof FioSplitLayout> = {
  title: "Layouts/FioSplitLayout",
  component: FioSplitLayout,
  tags: ["autodocs"],
  parameters: {
    layout: "fullscreen",
  },
  argTypes: {
    columns: {
      control: "text",
      description:
        "CSS grid-template-columns value (e.g. '7fr 5fr'). Defaults to '1fr 1fr'.",
    },
  },
};

export default meta;
type Story = StoryObj<typeof FioSplitLayout>;

export const Default: Story = {
  render: () => ({
    template: `
            <fio-split-layout>
                <template #left>
                    <div style="padding: 2rem; background: #f0f0f0;">Left slot</div>
                </template>
                <template #right>
                    <div style="padding: 2rem; background: #e0e0e0;">Right slot</div>
                </template>
            </fio-split-layout>
        `,
  }),
};

export const CustomRatio: Story = {
  render: () => ({
    template: `
            <fio-split-layout columns="7fr 5fr">
                <template #left>
                    <div style="padding: 2rem; background: #f0f0f0;">Content (7fr)</div>
                </template>
                <template #right>
                    <div style="padding: 2rem; background: #e0e0e0;">Sidebar (5fr)</div>
                </template>
            </fio-split-layout>
        `,
  }),
};
