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
    label: {
      control: "text",
      description:
        "Label displayed above the input. Omit to render without a label.",
    },
    pattern: {
      control: false,
      description:
        "RegExp — filters keystrokes so the value always matches. Also shows --invalid class when current value does not match.",
    },
    "onUpdate:modelValue": { action: "update:modelValue" },
    "onUpdate:isValid": { action: "update:isValid" },
    onInput: { action: "input" },
  },
};

export default meta;
type Story = StoryObj<typeof FioInputText>;

export const WithoutLabel: Story = {
  name: "Without Label",
  args: {
    modelValue: "",
    placeholder: "Search...",
  },
};

export const WithLabel: Story = {
  name: "With Label",
  args: {
    modelValue: "",
    label: "Code OTP (6 chiffres)",
    placeholder: "Code OTP (6 chiffres)",
  },
};

export const PreFilled: Story = {
  name: "Pre-filled",
  args: {
    modelValue: "Hello world",
    label: "Text field",
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
        <fio-input-text v-model="text" label="Your text" placeholder="Type something..." />
        <div style="margin-top: 0.5rem; color: #6c757d; font-size: 0.875rem;">
          Value: <code>{{ text || "(empty)" }}</code>
        </div>
      </div>
    `,
  }),
};

export const OtpPattern: Story = {
  name: "OTP Pattern (digits only, max 6)",
  render: () => ({
    components: { FioInputText },
    setup() {
      const code = ref("");
      const isValid = ref(true);
      return { code, isValid, pattern: /^\d{0,6}$/ };
    },
    template: `
      <div style="width: 100%;">
        <fio-input-text
          v-model="code"
          v-model:isValid="isValid"
          :pattern="pattern"
          label="Code OTP (6 chiffres)"
          placeholder="Enter 6-digit code"
        />
        <div style="margin-top: 0.5rem; color: #6c757d; font-size: 0.875rem;">
          Value: <code>{{ code || "(empty)" }}</code> — Length: {{ code.length }}/6 — Valid: {{ isValid }}
        </div>
      </div>
    `,
  }),
};
