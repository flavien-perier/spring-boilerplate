import type { Meta, StoryObj } from "@storybook/vue3";
import { ref } from "vue";
import FioModal from "./modal.vue";

const meta: Meta<typeof FioModal> = {
  title: "Components/FioModal",
  component: FioModal,
  tags: ["autodocs"],
  argTypes: {
    show: {
      control: "boolean",
      description: "Controls modal visibility",
    },
    title: {
      control: "text",
      description: "Modal title text",
    },
    content: {
      control: "text",
      description: "Modal body content text",
    },
    rejectMessage: {
      control: "text",
      description: "Label for the reject/cancel button — hidden when empty",
    },
    resolveMessage: {
      control: "text",
      description: "Label for the confirm/resolve button — hidden when empty",
    },
    onClose: { action: "close" },
    onResolve: { action: "resolve" },
  },
};

export default meta;
type Story = StoryObj<typeof FioModal>;

export const Default: Story = {
  args: {
    show: true,
    title: "Confirm action",
    content: "Are you sure you want to proceed?",
    rejectMessage: "Cancel",
    resolveMessage: "Confirm",
  },
};

export const ConfirmOnly: Story = {
  name: "Confirm Only",
  args: {
    show: true,
    title: "Delete item",
    content: "This action cannot be undone.",
    rejectMessage: "",
    resolveMessage: "Delete",
  },
};

export const InfoOnly: Story = {
  name: "Info Only",
  args: {
    show: true,
    title: "Information",
    content: "Your session has been updated.",
    rejectMessage: "",
    resolveMessage: "",
  },
};

export const Hidden: Story = {
  args: {
    show: false,
    title: "Hidden modal",
    content: "You should not see this.",
    rejectMessage: "Cancel",
    resolveMessage: "Confirm",
  },
};

export const Interactive: Story = {
  render: () => ({
    components: { FioModal },
    setup() {
      const show = ref(true);
      return { show };
    },
    template: `
            <div>
                <button @click="show = true">Open modal</button>
                <fio-modal
                    :show="show"
                    title="Confirm action"
                    content="Are you sure you want to proceed?"
                    rejectMessage="Cancel"
                    resolveMessage="Confirm"
                    @close="show = false"
                    @resolve="show = false"
                />
            </div>
        `,
  }),
};
