import type {Meta, StoryObj} from "@storybook/vue3";
import {ref} from "vue";
import FioInputToggle from "./input-toggle.vue";

const meta: Meta<typeof FioInputToggle> = {
  title: "Components/Inputs/FioInputToggle",
  component: FioInputToggle,
  tags: ["autodocs"],
  argTypes: {
    modelValue: {
      control: "boolean",
      description: "Current toggled state (v-model)",
    },
    label: {
      control: "text",
      description: "Label displayed next to the toggle",
    },
    disabled: {
      control: "boolean",
      description: "Disables the toggle when true",
    },
    "onUpdate:modelValue": {action: "update:modelValue"},
    onChange: {action: "change"},
  },
};

export default meta;
type Story = StoryObj<typeof FioInputToggle>;

export const Default: Story = {
  render: () => ({
    setup() {
      const toggled = ref(false);
      const disabled = ref(false);
      return {toggled, disabled};
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <fio-input-toggle v-model="toggled" label="Enable notifications" :disabled="disabled"/>
        <div style="color: #6c757d; font-size: 0.875rem;">
          Toggled: <code>{{ toggled }}</code>
        </div>
        <div style="display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled"/>
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
      const toggled = ref(false);
      return {toggled};
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <fio-input-toggle v-model="toggled"/>
        <div style="color: #6c757d; font-size: 0.875rem;">
          Toggled: <code>{{ toggled }}</code>
        </div>
      </div>
    `,
  }),
};

export const PreToggled: Story = {
  name: "Pre-toggled",
  render: () => ({
    setup() {
      const toggled = ref(true);
      return {toggled};
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <fio-input-toggle v-model="toggled" label="Dark mode"/>
        <div style="color: #6c757d; font-size: 0.875rem;">
          Toggled: <code>{{ toggled }}</code>
        </div>
      </div>
    `,
  }),
};

export const Disabled: Story = {
  name: "Disabled",
  render: () => ({
    setup() {
      const toggled = ref(true);
      return {toggled};
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <fio-input-toggle v-model="toggled" label="Cannot change this" :disabled="true"/>
      </div>
    `,
  }),
};

export const AllStates: Story = {
  name: "All States",
  render: () => ({
    setup() {
      const off = ref(false);
      const on = ref(true);
      const offDisabled = ref(false);
      const onDisabled = ref(true);
      return {off, on, offDisabled, onDisabled};
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem; width: 300px;">
        <fio-input-toggle v-model="off" label="Off"/>
        <fio-input-toggle v-model="on" label="On"/>
        <fio-input-toggle v-model="offDisabled" label="Off (disabled)" :disabled="true"/>
        <fio-input-toggle v-model="onDisabled" label="On (disabled)" :disabled="true"/>
      </div>
    `,
  }),
};
