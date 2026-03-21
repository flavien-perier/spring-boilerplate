import type { Meta, StoryObj } from "@storybook/vue3";
import { ref } from "vue";
import FioInputText from "./input-text.vue";

const meta: Meta<typeof FioInputText> = {
  title: "Components/Inputs/FioInputText",
  component: FioInputText,
  tags: ["autodocs"],
  argTypes: {
    modelValue: {
      control: "text",
      description: "Current text value (v-model)",
    },
    placeholder: {
      control: "text",
      description: "Placeholder text",
    },
    "onUpdate:modelValue": { action: "update:modelValue" },
    onInput: { action: "input" },
  },
};

export default meta;
type Story = StoryObj<typeof FioInputText>;

export const Empty: Story = {
  args: {
    modelValue: "",
  },
};

export const WithPlaceholder: Story = {
  name: "With Placeholder",
  args: {
    modelValue: "",
    placeholder: "Search...",
  },
};

export const PreFilled: Story = {
  name: "Pre-filled",
  args: {
    modelValue: "Hello world",
  },
};

export const Interactive: Story = {
  name: "Interactive (v-model)",
  render: () => ({
    components: { FioInputText },
    setup() {
      const text = ref("");
      return { text };
    },
    template: `
            <div style="width: 100%;">
                <fio-input-text v-model="text" placeholder="Type something..." />
                <div style="margin-top: 0.5rem; color: #6c757d; font-size: 0.875rem;">
                    Value: <code>{{ text || "(empty)" }}</code>
                </div>
            </div>
        `,
  }),
};
