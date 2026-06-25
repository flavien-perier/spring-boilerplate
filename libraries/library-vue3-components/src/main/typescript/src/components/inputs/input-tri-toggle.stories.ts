import type { Meta, StoryObj } from "@storybook/vue3";
import { ref } from "vue";
import FioInputTriToggle from "./input-tri-toggle.vue";

const meta: Meta<typeof FioInputTriToggle> = {
  title: "Components/Inputs/FioInputTriToggle",
  component: FioInputTriToggle,
  tags: ["autodocs"],
  argTypes: {
    modelValue: {
      control: "select",
      options: [true, false, null],
      description: "Current state: true / false / null (v-model)",
    },
    label: {
      control: "text",
      description: "Label displayed next to the toggle",
    },
    disabled: {
      control: "boolean",
      description: "Disables the toggle when true",
    },
    "onUpdate:modelValue": { action: "update:modelValue" },
    onChange: { action: "change" },
  },
};

export default meta;
type Story = StoryObj<typeof FioInputTriToggle>;

export const Default: Story = {
  render: () => ({
    setup() {
      const value = ref<boolean | null>(null);
      const disabled = ref(false);
      return { value, disabled };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <fio-input-tri-toggle v-model="value" label="Notifications" :disabled="disabled"/>
        <div style="color: #6c757d; font-size: 0.875rem;">
          Value: <code>{{ value === null ? 'null' : value }}</code>
        </div>
        <div style="display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled"/>
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};

export const NullState: Story = {
  name: "Null State",
  render: () => ({
    setup() {
      const value = ref<boolean | null>(null);
      return { value };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <fio-input-tri-toggle v-model="value" label="Inherit (null)"/>
        <div style="color: #6c757d; font-size: 0.875rem;">
          Value: <code>{{ value === null ? 'null' : value }}</code>
        </div>
      </div>
    `,
  }),
};

export const TrueState: Story = {
  name: "True State",
  render: () => ({
    setup() {
      const value = ref<boolean | null>(true);
      return { value };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <fio-input-tri-toggle v-model="value" label="Allowed"/>
        <div style="color: #6c757d; font-size: 0.875rem;">
          Value: <code>{{ value === null ? 'null' : value }}</code>
        </div>
      </div>
    `,
  }),
};

export const FalseState: Story = {
  name: "False State",
  render: () => ({
    setup() {
      const value = ref<boolean | null>(false);
      return { value };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <fio-input-tri-toggle v-model="value" label="Denied"/>
        <div style="color: #6c757d; font-size: 0.875rem;">
          Value: <code>{{ value === null ? 'null' : value }}</code>
        </div>
      </div>
    `,
  }),
};

export const Disabled: Story = {
  name: "Disabled",
  render: () => ({
    setup() {
      const value = ref<boolean | null>(null);
      return { value };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <fio-input-tri-toggle v-model="value" label="Cannot change this" :disabled="true"/>
      </div>
    `,
  }),
};

export const AllStates: Story = {
  name: "All States",
  render: () => ({
    setup() {
      const off = ref(false);
      const indeterminate = ref(null);
      const on = ref(true);
      const offDisabled = ref(false);
      const nullDisabled = ref(null);
      const onDisabled = ref(true);
      return { off, indeterminate, on, offDisabled, nullDisabled, onDisabled };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem; width: 300px;">
        <fio-input-tri-toggle v-model="off" label="False"/>
        <fio-input-tri-toggle v-model="indeterminate" label="Null"/>
        <fio-input-tri-toggle v-model="on" label="True"/>
        <fio-input-tri-toggle v-model="offDisabled" label="False (disabled)" :disabled="true"/>
        <fio-input-tri-toggle v-model="nullDisabled" label="Null (disabled)" :disabled="true"/>
        <fio-input-tri-toggle v-model="onDisabled" label="True (disabled)" :disabled="true"/>
      </div>
    `,
  }),
};
