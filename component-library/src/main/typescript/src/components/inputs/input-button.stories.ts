import type { Meta, StoryObj } from "@storybook/vue3";
import FioInputButton from "./input-button.vue";

const meta: Meta<typeof FioInputButton> = {
  title: "Components/Inputs/FioInputButton",
  component: FioInputButton,
  tags: ["autodocs"],
  argTypes: {
    label: {
      control: "text",
      description: "Button label text",
    },
    disabled: {
      control: "boolean",
      description: "Disables the button when true",
    },
    variant: {
      control: "select",
      options: ["submit", "danger"],
      description:
        'Variant: "submit" renders a full-width primary button, any other value renders a danger button',
    },
    icon: {
      control: "select",
      options: [
        undefined,
        "home",
        "user",
        "flag",
        "gear",
        "trash",
        "spinner",
        "right-to-bracket",
        "circle-check",
        "circle-xmark",
        "eye",
        "eye-slash",
        "angle-left",
        "angle-right",
        "xmark",
      ],
      description:
        "Optional FontAwesome solid icon displayed before the label",
    },
    waiting: {
      control: "boolean",
      description:
        "When true, disables the button and replaces the icon with a spinner",
    },
    onClick: { action: "clicked" },
  },
};

export default meta;
type Story = StoryObj<typeof FioInputButton>;

export const Submit: Story = {
  args: {
    label: "Submit",
    disabled: false,
    variant: "submit",
  },
};

export const SubmitDisabled: Story = {
  name: "Submit (Disabled)",
  args: {
    label: "Submit",
    disabled: true,
    variant: "submit",
  },
};

export const Danger: Story = {
  args: {
    label: "Delete",
    disabled: false,
    variant: "danger",
  },
};

export const DangerDisabled: Story = {
  name: "Danger (Disabled)",
  args: {
    label: "Delete",
    disabled: true,
    variant: "danger",
  },
};

export const Waiting: Story = {
  name: "Submit (Waiting)",
  args: {
    label: "Submit",
    waiting: true,
    variant: "submit",
  },
};

export const WaitingWithIcon: Story = {
  name: "Submit with Icon (Waiting)",
  args: {
    label: "Submit",
    waiting: true,
    variant: "submit",
    icon: "circle-check",
  },
};

export const WithIcon: Story = {
  name: "Submit with Icon",
  args: {
    label: "Submit",
    disabled: false,
    variant: "submit",
    icon: "circle-check",
  },
};

export const DangerWithIcon: Story = {
  name: "Danger with Icon",
  args: {
    label: "Delete",
    disabled: false,
    variant: "danger",
    icon: "trash",
  },
};

export const AllVariants: Story = {
  name: "All Variants",
  render: () => ({
    template: `
            <div style="display: flex; flex-direction: column; gap: 1rem; width: 300px;">
                <fio-input-button label="Submit Button" variant="submit" />
                <fio-input-button label="Submit (Disabled)" variant="submit" :disabled="true" />
                <fio-input-button label="Danger Button" variant="danger" />
                <fio-input-button label="Danger (Disabled)" variant="danger" :disabled="true" />
                <fio-input-button label="Submit with Icon" variant="submit" icon="circle-check" />
                <fio-input-button label="Delete with Icon" variant="danger" icon="trash" />
                <fio-input-button label="Waiting..." variant="submit" :waiting="true" />
                <fio-input-button label="Waiting with Icon" variant="submit" icon="circle-check" :waiting="true" />
            </div>
        `,
  }),
};
