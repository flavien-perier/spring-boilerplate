import type {Meta, StoryObj} from "@storybook/vue3";
import {ref} from "vue";
import FioInputMarkdown from "./input-markdown.vue";

const meta: Meta<typeof FioInputMarkdown> = {
  title: "Components/Inputs/FioInputMarkdown",
  component: FioInputMarkdown,
  tags: ["autodocs"],
  argTypes: {
    modelValue: {
      control: "text",
      description: "Raw markdown value (v-model)",
    },
    label: {
      control: "text",
      description: "Label displayed above the editor. Omit to hide.",
    },
    placeholder: {
      control: "text",
      description: "Placeholder shown when the editor is empty",
    },
    disabled: {
      control: "boolean",
      description: "Disables editing when true",
    },
    showToolbar: {
      control: "boolean",
      description:
        "Show the formatting toolbar above the editor. Default: true.",
      defaultValue: true,
    },
    enableTitle: {
      control: "boolean",
      description:
        "Show the heading button and render headings in the live preview. Default: true.",
      defaultValue: true,
    },
    enableLinks: {
      control: "boolean",
      description:
        "Show the link button and render links in the live preview. Default: true.",
      defaultValue: true,
    },
    "onUpdate:modelValue": {action: "update:modelValue"},
    onInput: {action: "input"},
  },
};

export default meta;
type Story = StoryObj<typeof FioInputMarkdown>;

const preview = `
  <div style="width: 100%; max-width: 640px;">
    <fio-input-markdown
      v-model="value"
      :label="label"
      :placeholder="placeholder"
      :disabled="disabled"
      :show-toolbar="showToolbar"
      :enable-title="enableTitle"
      :enable-links="enableLinks"
    />
    <div style="margin-top: 1rem;">
      <div style="color: #6c757d; font-size: 0.875rem; margin-bottom: 0.25rem;">Raw markdown value:</div>
      <pre style="background: #f4f4f4; padding: 0.5rem; border-radius: 4px; white-space: pre-wrap;"><code>{{ value || "(empty)" }}</code></pre>
    </div>
    <div style="margin-top: 1rem;">
      <div style="color: #6c757d; font-size: 0.875rem; margin-bottom: 0.25rem;">Rendered output:</div>
      <fio-markdown :source="value" />
    </div>
  </div>
`;

export const Default: Story = {
  render: () => ({
    setup() {
      const value = ref("");
      return {
        value,
        label: "",
        placeholder: "Write some markdown...",
        disabled: false,
        showToolbar: true,
        enableTitle: true,
        enableLinks: true,
      };
    },
    template: preview,
  }),
};

export const WithLabel: Story = {
  name: "With Label",
  render: () => ({
    setup() {
      const value = ref("");
      return {
        value,
        label: "Description",
        placeholder: "Write some markdown...",
        disabled: false,
        showToolbar: true,
        enableTitle: true,
        enableLinks: true,
      };
    },
    template: preview,
  }),
};

export const Prefilled: Story = {
  render: () => ({
    setup() {
      const value = ref(
        "# Title\n\n**bold** and *italic*\n\n- item 1\n- item 2"
      );
      return {
        value,
        label: "Content",
        placeholder: "",
        disabled: false,
        showToolbar: true,
        enableTitle: true,
        enableLinks: true,
      };
    },
    template: preview,
  }),
};

export const Disabled: Story = {
  render: () => ({
    setup() {
      const value = ref("# Read only\n\nThis content **cannot** be edited.");
      return {
        value,
        label: "Content",
        placeholder: "",
        disabled: true,
        showToolbar: true,
        enableTitle: true,
        enableLinks: true,
      };
    },
    template: preview,
  }),
};

export const NoToolbar: Story = {
  name: "Toolbar hidden",
  render: () => ({
    setup() {
      const value = ref("# No toolbar\n\nType **markdown** by hand.");
      return {
        value,
        label: "Content",
        placeholder: "",
        disabled: false,
        showToolbar: false,
        enableTitle: true,
        enableLinks: true,
      };
    },
    template: preview,
  }),
};

export const TitlesDisabled: Story = {
  name: "Titles disabled",
  render: () => ({
    setup() {
      const value = ref("# Not a title\n\nPlain paragraph.");
      return {
        value,
        label: "Content",
        placeholder: "",
        disabled: false,
        showToolbar: true,
        enableTitle: false,
        enableLinks: true,
      };
    },
    template: preview,
  }),
};

export const LinksDisabled: Story = {
  name: "Links disabled",
  render: () => ({
    setup() {
      const value = ref("No [link](https://example.com) here.");
      return {
        value,
        label: "Content",
        placeholder: "",
        disabled: false,
        showToolbar: true,
        enableTitle: true,
        enableLinks: false,
      };
    },
    template: preview,
  }),
};
