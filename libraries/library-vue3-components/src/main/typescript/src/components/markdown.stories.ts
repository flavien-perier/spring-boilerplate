import type {Meta, StoryObj} from "@storybook/vue3";
import {ref} from "vue";
import FioMarkdown from "./markdown.vue";

const meta: Meta<typeof FioMarkdown> = {
  title: "Components/FioMarkdown",
  component: FioMarkdown,
  tags: ["autodocs"],
  argTypes: {
    source: {
      control: "text",
      description:
        "Raw markdown string. Rendered to clean, semantic, XSS-safe HTML.",
    },
    enableTitle: {
      control: "boolean",
      description: "Render `#`-headings. Default: true.",
      defaultValue: true,
    },
    enableLinks: {
      control: "boolean",
      description: "Render `[label](url)` links. Default: true.",
      defaultValue: true,
    },
  },
};

export default meta;
type Story = StoryObj<typeof FioMarkdown>;

const richSample = [
  "# Markdown demo",
  "",
  "A paragraph with **bold**, *italic*, ~~strike~~ and `inline code`.",
  "",
  "## Lists",
  "",
  "- First item",
  "- Second item",
  "- Third item",
  "",
  "1. One",
  "2. Two",
  "",
  "## Code block",
  "",
  "```",
  "const answer = 42;",
  "console.log(answer);",
  "```",
  "",
  "> A blockquote with some wise words.",
  "",
  "See [the example site](https://example.com) for more.",
  "",
  "---",
].join("\n");

const shell = `
  <div style="width: 100%; max-width: 640px;">
    <fio-markdown
      :source="source"
      :enable-title="enableTitle"
      :enable-links="enableLinks"
    />
  </div>
`;

export const Default: Story = {
  render: () => ({
    setup() {
      const source = ref(richSample);
      return {source, enableTitle: true, enableLinks: true};
    },
    template: shell,
  }),
};

export const Inline: Story = {
  render: () => ({
    setup() {
      const source = ref(
        "Inline formatting: **bold**, *italic*, ~~strike~~, `code` and a [link](https://example.com)."
      );
      return {source, enableTitle: true, enableLinks: true};
    },
    template: shell,
  }),
};

export const CodeBlock: Story = {
  render: () => ({
    setup() {
      const source = ref(
        [
          "```",
          "function greet(name) {",
          "  return `Hello, ${name}!`;",
          "}",
          "```",
        ].join("\n")
      );
      return {source, enableTitle: true, enableLinks: true};
    },
    template: shell,
  }),
};

export const Sanitized: Story = {
  name: "Sanitized (XSS-safe)",
  render: () => ({
    setup() {
      const source = ref(
        [
          "Raw HTML is neutralized:",
          "",
          "<script>alert('xss')</scr" + "ipt>",
          "",
          "And dangerous links are stripped: [click me](javascript:alert(1)).",
        ].join("\n")
      );
      return {source, enableTitle: true, enableLinks: true};
    },
    template: `
      <div style="width: 100%; max-width: 640px;">
        <fio-markdown :source="source" />
        <p style="margin-top: 0.75rem; color: #6c757d; font-size: 0.875rem;">
          The script tag is rendered as inert text and the
          <code>javascript:</code> link resolves to <code>#</code> — no code runs.
        </p>
      </div>
    `,
  }),
};

export const TitlesDisabled: Story = {
  name: "Titles disabled",
  render: () => ({
    setup() {
      const source = ref(richSample);
      return {source, enableTitle: false, enableLinks: true};
    },
    template: shell,
  }),
};

export const LinksDisabled: Story = {
  name: "Links disabled",
  render: () => ({
    setup() {
      const source = ref(richSample);
      return {source, enableTitle: true, enableLinks: false};
    },
    template: shell,
  }),
};

export const Empty: Story = {
  render: () => ({
    setup() {
      const source = ref("");
      return {source, enableTitle: true, enableLinks: true};
    },
    template: `
      <div style="width: 100%; max-width: 640px;">
        <fio-markdown :source="source"/>
        <p style="color: #6c757d; font-size: 0.875rem;">(empty source renders nothing)</p>
      </div>
    `,
  }),
};
