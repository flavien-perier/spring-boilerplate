import type { Meta, StoryObj } from "@storybook/vue3";
import { ref } from "vue";
import FioAlert from "./alert.vue";

const meta: Meta<typeof FioAlert> = {
  title: "Components/FioAlert",
  component: FioAlert,
  tags: ["autodocs"],
  argTypes: {
    type: {
      control: "select",
      options: ["info", "danger"],
      description:
        "Visual style of the alert — 'info' renders blue, 'danger' renders red",
    },
    message: {
      control: "text",
      description: "Already-translated message text displayed inside the alert",
    },
    onClose: { action: "close" },
  },
};

export default meta;
type Story = StoryObj<typeof FioAlert>;

export const Info: Story = {
  args: {
    type: "info",
    message: "This is an informational message.",
  },
};

export const Danger: Story = {
  name: "Danger",
  args: {
    type: "danger",
    message: "Something went wrong. Please try again.",
  },
};

export const Interactive: Story = {
  render: () => ({
    components: { FioAlert },
    setup() {
      const showInfo = ref(true);
      const showDanger = ref(true);
      return { showInfo, showDanger };
    },
    template: `
            <div style="display: flex; flex-direction: column; gap: 0.5rem;">
                <button style="width: fit-content;" @click="showInfo = true; showDanger = true">
                    Reset
                </button>
                <fio-alert
                    v-if="showInfo"
                    type="info"
                    message="This is an informational message."
                    @close="showInfo = false"
                />
                <fio-alert
                    v-if="showDanger"
                    type="danger"
                    message="Something went wrong. Please try again."
                    @close="showDanger = false"
                />
            </div>
        `,
  }),
};

export const AllTypes: Story = {
  name: "All Types",
  render: () => ({
    components: { FioAlert },
    template: `
            <div style="display: flex; flex-direction: column; gap: 0.5rem;">
                <fio-alert type="info" message="This is an informational message." @close="() => {}" />
                <fio-alert type="danger" message="Something went wrong. Please try again." @close="() => {}" />
            </div>
        `,
  }),
};
