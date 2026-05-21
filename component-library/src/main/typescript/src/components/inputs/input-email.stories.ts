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
    disabled: {
      control: "boolean",
      description: "Disables the input when true",
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
  name: "Empty",
  render: () => ({
    setup() {
      const email = ref("");
      const isValid = ref(false);
      const disabled = ref(false);
      return { email, isValid, disabled };
    },
    template: `
      <div style="width: 320px;">
        <fio-input-email v-model="email" v-model:isValid="isValid" :disabled="disabled" />
        <div style="margin-top: 0.5rem; font-size: 0.875rem; color: #6c757d;">
          Value: <code>{{ email || "(empty)" }}</code><br />
          Valid: <code :style="{ color: isValid ? 'green' : 'red' }">{{ isValid }}</code>
        </div>
        <div style="margin-top: 0.5rem; display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled" />
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};

export const ValidEmail: Story = {
  name: "Valid Email",
  render: () => ({
    setup() {
      const email = ref("user@example.com");
      const isValid = ref(false);
      const disabled = ref(false);
      return { email, isValid, disabled };
    },
    template: `
      <div style="width: 320px;">
        <fio-input-email v-model="email" v-model:isValid="isValid" :disabled="disabled" />
        <div style="margin-top: 0.5rem; font-size: 0.875rem; color: #6c757d;">
          Value: <code>{{ email || "(empty)" }}</code><br />
          Valid: <code :style="{ color: isValid ? 'green' : 'red' }">{{ isValid }}</code>
        </div>
        <div style="margin-top: 0.5rem; display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled" />
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};

export const InvalidEmail: Story = {
  name: "Invalid Email (shows error)",
  render: () => ({
    setup() {
      const email = ref("not-an-email");
      const isValid = ref(false);
      const disabled = ref(false);
      return { email, isValid, disabled };
    },
    template: `
      <div style="width: 320px;">
        <fio-input-email v-model="email" v-model:isValid="isValid" :disabled="disabled" />
        <div style="margin-top: 0.5rem; font-size: 0.875rem; color: #6c757d;">
          Value: <code>{{ email || "(empty)" }}</code><br />
          Valid: <code :style="{ color: isValid ? 'green' : 'red' }">{{ isValid }}</code>
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
      const email = ref("user@example.com");
      const isValid = ref(false);
      const disabled = ref(true);
      return { email, isValid, disabled };
    },
    template: `
      <div style="width: 320px;">
        <fio-input-email v-model="email" v-model:isValid="isValid" :disabled="disabled" />
        <div style="margin-top: 0.5rem; font-size: 0.875rem; color: #6c757d;">
          Value: <code>{{ email || "(empty)" }}</code><br />
          Valid: <code :style="{ color: isValid ? 'green' : 'red' }">{{ isValid }}</code>
        </div>
        <div style="margin-top: 0.5rem; display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled" />
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};
