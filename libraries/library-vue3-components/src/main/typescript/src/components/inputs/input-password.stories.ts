import type { Meta, StoryObj } from "@storybook/vue3";
import { ref } from "vue";
import FioInputPassword from "./input-password.vue";

const meta: Meta<typeof FioInputPassword> = {
  title: "Components/Inputs/FioInputPassword",
  component: FioInputPassword,
  tags: ["autodocs"],
  argTypes: {
    modelValue: {
      control: "text",
      description: "Current password value (v-model)",
    },
    label: {
      control: "text",
      description: "Label text displayed above the input",
    },
    isValid: {
      control: "boolean",
      description:
        "Controls whether the invalid CSS class is applied (validation is driven externally)",
    },
    disabled: {
      control: "boolean",
      description: "Disables the input when true",
    },
    "onUpdate:modelValue": { action: "update:modelValue" },
    onInput: { action: "input" },
    onFocus: { action: "focus" },
    onBlur: { action: "blur" },
  },
};

export default meta;
type Story = StoryObj<typeof FioInputPassword>;

export const Default: Story = {
  name: "Default",
  render: () => ({
    setup() {
      const password = ref("");
      const isValid = ref(true);
      const disabled = ref(false);
      return { password, isValid, disabled };
    },
    template: `
      <div style="width: 320px;">
        <fio-input-password v-model="password" label="Password" :is-valid="isValid" :disabled="disabled" />
        <div style="margin-top: 0.5rem; font-size: 0.875rem; color: #6c757d;">
          Value: <code>{{ password || "(empty)" }}</code>
        </div>
        <div style="margin-top: 0.5rem; display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled" />
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};

export const Invalid: Story = {
  name: "Invalid State",
  render: () => ({
    setup() {
      const password = ref("weak");
      const isValid = ref(false);
      const disabled = ref(false);
      return { password, isValid, disabled };
    },
    template: `
      <div style="width: 320px;">
        <fio-input-password v-model="password" label="Password" :is-valid="isValid" :disabled="disabled" />
        <div style="margin-top: 0.5rem; font-size: 0.875rem; color: #6c757d;">
          Value: <code>{{ password || "(empty)" }}</code>
        </div>
        <div style="margin-top: 0.5rem; display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled" />
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};

export const CustomLabel: Story = {
  name: "Custom Label",
  render: () => ({
    setup() {
      const password = ref("");
      const isValid = ref(true);
      const disabled = ref(false);
      return { password, isValid, disabled };
    },
    template: `
      <div style="width: 320px;">
        <fio-input-password v-model="password" label="Confirm Password" :is-valid="isValid" :disabled="disabled" />
        <div style="margin-top: 0.5rem; font-size: 0.875rem; color: #6c757d;">
          Value: <code>{{ password || "(empty)" }}</code>
        </div>
        <div style="margin-top: 0.5rem; display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled" />
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};
