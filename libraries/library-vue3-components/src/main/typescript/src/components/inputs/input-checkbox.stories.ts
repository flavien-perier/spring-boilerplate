import type {Meta, StoryObj} from "@storybook/vue3";
import {ref} from "vue";
import FioInputCheckbox from "./input-checkbox.vue";

const meta: Meta<typeof FioInputCheckbox> = {
  title: "Components/Inputs/FioInputCheckbox",
  component: FioInputCheckbox,
  tags: ["autodocs"],
  argTypes: {
    modelValue: {
      control: "boolean",
      description: "Current checked state (v-model)",
    },
    label: {
      control: "text",
      description: "Label displayed next to the checkbox",
    },
    disabled: {
      control: "boolean",
      description: "Disables the checkbox when true",
    },
    "onUpdate:modelValue": {action: "update:modelValue"},
    onChange: {action: "change"},
  },
};

export default meta;
type Story = StoryObj<typeof FioInputCheckbox>;

export const Default: Story = {
  render: () => ({
    setup() {
      const checked = ref(false);
      const disabled = ref(false);
      return {checked, disabled};
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <fio-input-checkbox v-model="checked" label="Accept terms and conditions" :disabled="disabled" />
        <div style="color: #6c757d; font-size: 0.875rem;">
          Checked: <code>{{ checked }}</code>
        </div>
        <div style="display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled" />
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};

export const WithoutLabel: Story = {
  name: "Without Label",
  render: () => ({
    setup() {
      const checked = ref(false);
      return {checked};
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <fio-input-checkbox v-model="checked" />
        <div style="color: #6c757d; font-size: 0.875rem;">
          Checked: <code>{{ checked }}</code>
        </div>
      </div>
    `,
  }),
};

export const PreChecked: Story = {
  name: "Pre-checked",
  render: () => ({
    setup() {
      const checked = ref(true);
      return {checked};
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <fio-input-checkbox v-model="checked" label="Subscribe to newsletter" />
        <div style="color: #6c757d; font-size: 0.875rem;">
          Checked: <code>{{ checked }}</code>
        </div>
      </div>
    `,
  }),
};

export const Disabled: Story = {
  name: "Disabled",
  render: () => ({
    setup() {
      const checked = ref(true);
      return {checked};
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <fio-input-checkbox v-model="checked" label="Cannot change this" :disabled="true" />
      </div>
    `,
  }),
};
