import type { Meta, StoryObj } from "@storybook/vue3";
import FioFooter from "./footer.vue";

const meta: Meta<typeof FioFooter> = {
    title: "Components/FioFooter",
    component: FioFooter,
    tags: ["autodocs"],
    parameters: {
        layout: "fullscreen",
    },
    argTypes: {
        href: {
            control: "text",
            description: "URL of the copyright link",
        },
        label: {
            control: "text",
            description: "Label text for the copyright link",
        },
    },
};

export default meta;
type Story = StoryObj<typeof FioFooter>;

export const Default: Story = {
    args: {
        href: "https://flavien.io",
        label: "Flavien PERIER",
    },
};

export const CustomLink: Story = {
    args: {
        href: "https://example.com",
        label: "My Company",
    },
};
