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
    disabled: {
      control: "boolean",
      description: "Disables the input when true",
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
  render: () => ({
    setup() {
      const text = ref("");
      const disabled = ref(false);
      return { text, disabled };
    },
    template: `
      <div style="width: 100%;">
        <fio-input-text v-model="text" placeholder="Search..." :disabled="disabled" />
        <div style="margin-top: 0.5rem; color: #6c757d; font-size: 0.875rem;">
          Value: <code>{{ text || "(empty)" }}</code>
        </div>
        <div style="margin-top: 0.5rem; display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled" />
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};

export const WithLabel: Story = {
  name: "With Label",
  render: () => ({
    setup() {
      const text = ref("");
      const disabled = ref(false);
      return { text, disabled };
    },
    template: `
      <div style="width: 100%;">
        <fio-input-text v-model="text" label="Code OTP (6 chiffres)" placeholder="Code OTP (6 chiffres)" :disabled="disabled" />
        <div style="margin-top: 0.5rem; color: #6c757d; font-size: 0.875rem;">
          Value: <code>{{ text || "(empty)" }}</code>
        </div>
        <div style="margin-top: 0.5rem; display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled" />
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};

export const PreFilled: Story = {
  name: "Pre-filled",
  render: () => ({
    setup() {
      const text = ref("Hello world");
      const disabled = ref(false);
      return { text, disabled };
    },
    template: `
      <div style="width: 100%;">
        <fio-input-text v-model="text" label="Text field" :disabled="disabled" />
        <div style="margin-top: 0.5rem; color: #6c757d; font-size: 0.875rem;">
          Value: <code>{{ text || "(empty)" }}</code>
        </div>
        <div style="margin-top: 0.5rem; display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled" />
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};

export const Disabled: Story = {
  name: "Disabled",
  render: () => ({
    setup() {
      const text = ref("123456");
      const disabled = ref(true);
      return { text, disabled };
    },
    template: `
      <div style="width: 100%;">
        <fio-input-text v-model="text" label="Code OTP" placeholder="Code OTP" :disabled="disabled" />
        <div style="margin-top: 0.5rem; color: #6c757d; font-size: 0.875rem;">
          Value: <code>{{ text || "(empty)" }}</code>
        </div>
        <div style="margin-top: 0.5rem; display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled" />
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};

export const OtpPattern: Story = {
  name: "OTP Pattern (digits only, max 6)",
  render: () => ({
    setup() {
      const code = ref("");
      const isValid = ref(true);
      const disabled = ref(false);
      return { code, isValid, disabled, pattern: /^\d{0,6}$/ };
    },
    template: `
      <div style="width: 100%;">
        <fio-input-text
          v-model="code"
          v-model:isValid="isValid"
          :pattern="pattern"
          label="Code OTP (6 chiffres)"
          placeholder="Enter 6-digit code"
          :disabled="disabled"
        />
        <div style="margin-top: 0.5rem; color: #6c757d; font-size: 0.875rem;">
          Value: <code>{{ code || "(empty)" }}</code> — Length: {{ code.length }}/6 — Valid: {{ isValid }}
        </div>
        <div style="margin-top: 0.5rem; display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled" />
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};
