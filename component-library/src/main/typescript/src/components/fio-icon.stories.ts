import type { Meta, StoryObj } from "@storybook/vue3";
import FioIcon from "./fio-icon.vue";

const meta: Meta<typeof FioIcon> = {
  title: "Components/FioIcon",
  component: FioIcon,
  tags: ["autodocs"],
  argTypes: {
    icon: {
      control: "select",
      options: [
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
      ],
      description: "Name of the FontAwesome solid icon to display",
    },
    color: {
      control: "select",
      options: ["primary", "secondary", "info", "success", "warning", "danger"],
      description:
        "Semantic color applied to the icon (inherits parent color when omitted)",
    },
  },
};

export default meta;
type Story = StoryObj<typeof FioIcon>;

export const Home: Story = {
  args: { icon: "home" },
};

export const User: Story = {
  args: { icon: "user" },
};

export const Settings: Story = {
  args: { icon: "gear" },
};

export const Trash: Story = {
  args: { icon: "trash" },
};

export const Spinner: Story = {
  args: { icon: "spinner" },
};

export const Login: Story = {
  args: { icon: "right-to-bracket" },
};

export const CircleCheck: Story = {
  args: { icon: "circle-check" },
};

export const CircleXmark: Story = {
  args: { icon: "circle-xmark" },
};

export const Eye: Story = {
  args: { icon: "eye" },
};

export const EyeSlash: Story = {
  args: { icon: "eye-slash" },
};

export const AllIcons: Story = {
  name: "All Available Icons",
  render: () => ({
    template: `
            <div style="display: flex; gap: 1rem; flex-wrap: wrap; font-size: 1.5rem; align-items: center;">
                <div v-for="icon in icons" :key="icon" style="display: flex; flex-direction: column; align-items: center; gap: 4px;">
                    <fio-icon :icon="icon" />
                    <span style="font-size: 0.6rem; color: #666;">{{ icon }}</span>
                </div>
            </div>
        `,
    data() {
      return {
        icons: [
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
        ],
      };
    },
  }),
};

export const Colors: Story = {
  name: "Semantic Colors",
  render: () => ({
    template: `
            <div style="display: flex; gap: 1.5rem; flex-wrap: wrap; font-size: 1.5rem; align-items: center;">
                <div v-for="color in colors" :key="color" style="display: flex; flex-direction: column; align-items: center; gap: 4px;">
                    <fio-icon icon="circle-check" :color="color" />
                    <span style="font-size: 0.6rem; color: #666;">{{ color }}</span>
                </div>
            </div>
        `,
    data() {
      return {
        colors: [
          "primary",
          "secondary",
          "info",
          "success",
          "warning",
          "danger",
        ],
      };
    },
  }),
};
