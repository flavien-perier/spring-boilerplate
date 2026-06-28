import type { Meta, StoryObj } from "@storybook/vue3";
import { ref } from "vue";
import FioInputSelect from "./input-select.vue";
import type { SelectOption } from "./input-select.vue";

const meta: Meta<typeof FioInputSelect> = {
  title: "Components/Inputs/FioInputSelect",
  component: FioInputSelect,
  tags: ["autodocs"],
  argTypes: {
    modelValue: {
      control: "text",
      description: "Current selected value (v-model)",
    },
    label: {
      control: "text",
      description: "Label displayed above the select",
    },
    nullOption: {
      control: "text",
      description: "Placeholder/null option label — renders an empty option",
    },
    options: {
      control: "object",
      description: "Selectable options ({ value, label, icon? }[])",
    },
    disabled: {
      control: "boolean",
      description: "Disables the select when true",
    },
    "onUpdate:modelValue": { action: "update:modelValue" },
    onChange: { action: "change" },
  },
};

export default meta;
type Story = StoryObj<typeof FioInputSelect>;

export const Default: Story = {
  render: () => ({
    setup() {
      const value = ref<string | null>(null);
      const disabled = ref(false);
      const options: SelectOption[] = [
        { value: "admin", label: "Admin" },
        { value: "user", label: "User" },
        { value: "guest", label: "Guest" },
      ];
      return { value, disabled, options };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem; width: 320px;">
        <fio-input-select
          v-model="value"
          label="Role"
          null-option="— None —"
          :options="options"
          :disabled="disabled"
        />
        <div style="color: #6c757d; font-size: 0.875rem;">
          Selected: <code>{{ value ?? "(null)" }}</code>
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
      const value = ref<string | null>(null);
      const options: SelectOption[] = [
        { value: "fr", label: "Français" },
        { value: "en", label: "English" },
      ];
      return { value, options };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem; width: 320px;">
        <fio-input-select v-model="value" :options="options" />
        <div style="color: #6c757d; font-size: 0.875rem;">
          Selected: <code>{{ value ?? "(null)" }}</code>
        </div>
      </div>
    `,
  }),
};

export const PreSelected: Story = {
  name: "Pre-selected",
  render: () => ({
    setup() {
      const value = ref<string | null>("user");
      const options: SelectOption[] = [
        { value: "admin", label: "Admin" },
        { value: "user", label: "User" },
        { value: "guest", label: "Guest" },
      ];
      return { value, options };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem; width: 320px;">
        <fio-input-select v-model="value" label="Role" :options="options" />
        <div style="color: #6c757d; font-size: 0.875rem;">
          Selected: <code>{{ value ?? "(null)" }}</code>
        </div>
      </div>
    `,
  }),
};

export const Disabled: Story = {
  name: "Disabled",
  render: () => ({
    setup() {
      const value = ref<string | null>("admin");
      const options: SelectOption[] = [
        { value: "admin", label: "Admin" },
        { value: "user", label: "User" },
      ];
      return { value, options };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem; width: 320px;">
        <fio-input-select v-model="value" label="Role" :options="options" :disabled="true" />
      </div>
    `,
  }),
};

export const WithIcons: Story = {
  name: "With Icons",
  render: () => ({
    setup() {
      const value = ref<string | null>(null);
      const options: SelectOption[] = [
        { value: "home", label: "Home", icon: "home" },
        { value: "user", label: "User", icon: "user" },
        { value: "users", label: "Users", icon: "users" },
        { value: "gear", label: "Settings", icon: "gear" },
      ];
      return { value, options };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem; width: 320px;">
        <fio-input-select
          v-model="value"
          label="Navigation"
          null-option="— Select —"
          :options="options"
        />
        <div style="color: #6c757d; font-size: 0.875rem;">
          Selected: <code>{{ value ?? "(null)" }}</code>
        </div>
      </div>
    `,
  }),
};

export const Small: Story = {
  name: "Small Size",
  render: () => ({
    setup() {
      const value = ref<string | null>("fr");
      const options: SelectOption[] = [
        { value: "fr", label: "Français" },
        { value: "en", label: "English" },
      ];
      return { value, options };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem; width: 200px;">
        <fio-input-select v-model="value" label="Lang" :options="options" size="s" />
        <div style="color: #6c757d; font-size: 0.875rem;">
          Selected: <code>{{ value ?? "(null)" }}</code>
        </div>
      </div>
    `,
  }),
};
