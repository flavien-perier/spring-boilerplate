import type { Meta, StoryObj } from "@storybook/vue3";
import { ref } from "vue";
import FioInputEmail from "./input-email.vue";

const meta: Meta<typeof FioInputEmail> = {
  title: "Components/Inputs/FioInputEmail",
  component: FioInputEmail,
  tags: ["autodocs"],
  argTypes: {
    modelValue: {
      control: "text",
      description: "Current email value (v-model)",
    },
    "onUpdate:modelValue": { action: "update:modelValue" },
    "onUpdate:isValid": { action: "update:isValid" },
    onInput: { action: "input" },
    onFocus: { action: "focus" },
    onBlur: { action: "blur" },
  },
};

export default meta;
type Story = StoryObj<typeof FioInputEmail>;

export const Empty: Story = {
  args: {
    modelValue: "",
  },
};

export const ValidEmail: Story = {
  name: "Valid Email",
  args: {
    modelValue: "user@example.com",
  },
};

export const InvalidEmail: Story = {
  name: "Invalid Email (shows error)",
  args: {
    modelValue: "not-an-email",
  },
};

export const Interactive: Story = {
  name: "Interactive (v-model)",
  render: () => ({
    setup() {
      const email = ref("");
      const isValid = ref(true);
      return { email, isValid };
    },
    template: `
            <div style="width: 320px;">
                <fio-input-email v-model="email" v-model:isValid="isValid" />
                <div style="margin-top: 8px; font-size: 0.85rem; color: #666;">
                    Value: <code>{{ email || "(empty)" }}</code><br />
                    Valid: <code :style="{ color: isValid ? 'green' : 'red' }">{{ isValid }}</code>
                </div>
            </div>
        `,
  }),
};
