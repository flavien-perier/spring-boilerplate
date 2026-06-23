import type { Meta, StoryObj } from "@storybook/vue3";
import { ref } from "vue";
import FioPermissionSetting from "./permission-setting.vue";

const meta: Meta<typeof FioPermissionSetting> = {
  title: "Components/FioPermissionSetting",
  component: FioPermissionSetting,
  tags: ["autodocs"],
  argTypes: {
    permission: {
      control: "text",
      description: "Permission name displayed as the label",
    },
    override: {
      control: "boolean",
      description: "Whether the permission is overridden (v-model:override)",
    },
    allow: {
      control: "boolean",
      description:
        "Allow (true) or deny (false) when overridden (v-model:allow)",
    },
    overrideLabel: {
      control: "text",
      description: "Label for the override checkbox",
    },
    allowLabel: {
      control: "text",
      description: "Label shown when allow is true",
    },
    denyLabel: {
      control: "text",
      description: "Label shown when allow is false",
    },
    disabled: {
      control: "boolean",
      description: "Disables the whole row when true",
    },
    "onUpdate:override": { action: "update:override" },
    "onUpdate:allow": { action: "update:allow" },
    onChange: { action: "change" },
  },
};

export default meta;
type Story = StoryObj<typeof FioPermissionSetting>;

export const Default: Story = {
  render: () => ({
    setup() {
      const override = ref(false);
      const allow = ref(false);
      const disabled = ref(false);
      return { override, allow, disabled };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem; width: 480px;">
        <fio-permission-setting
          v-model:override="override"
          v-model:allow="allow"
          permission="MANAGE_ALL_USERS"
          override-label="Override"
          allow-label="Allow"
          deny-label="Deny"
          :disabled="disabled"
        />
        <div style="color: #6c757d; font-size: 0.875rem;">
          Override: <code>{{ override }}</code> — Allow: <code>{{ allow }}</code>
        </div>
        <div style="display: flex; align-items: center; gap: 0.25rem;">
          <input type="checkbox" id="disabledCheck" v-model="disabled" />
          <label for="disabledCheck">Disabled</label>
        </div>
      </div>
    `,
  }),
};

export const Allowed: Story = {
  name: "Override + Allow",
  render: () => ({
    setup() {
      const override = ref(true);
      const allow = ref(true);
      return { override, allow };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem; width: 480px;">
        <fio-permission-setting
          v-model:override="override"
          v-model:allow="allow"
          permission="MANAGE_ALL_USERS"
          override-label="Override"
          allow-label="Allow"
          deny-label="Deny"
        />
        <div style="color: #6c757d; font-size: 0.875rem;">
          Override: <code>{{ override }}</code> — Allow: <code>{{ allow }}</code>
        </div>
      </div>
    `,
  }),
};

export const Denied: Story = {
  name: "Override + Deny",
  render: () => ({
    setup() {
      const override = ref(true);
      const allow = ref(false);
      return { override, allow };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem; width: 480px;">
        <fio-permission-setting
          v-model:override="override"
          v-model:allow="allow"
          permission="MANAGE_ALL_USERS"
          override-label="Override"
          allow-label="Allow"
          deny-label="Deny"
        />
        <div style="color: #6c757d; font-size: 0.875rem;">
          Override: <code>{{ override }}</code> — Allow: <code>{{ allow }}</code>
        </div>
      </div>
    `,
  }),
};

export const Disabled: Story = {
  name: "Disabled",
  render: () => ({
    setup() {
      const override = ref(true);
      const allow = ref(true);
      return { override, allow };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem; width: 480px;">
        <fio-permission-setting
          v-model:override="override"
          v-model:allow="allow"
          permission="MANAGE_ALL_USERS"
          override-label="Override"
          allow-label="Allow"
          deny-label="Deny"
          :disabled="true"
        />
      </div>
    `,
  }),
};
