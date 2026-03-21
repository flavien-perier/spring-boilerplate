import type { Meta, StoryObj } from "@storybook/vue3";
import FioNavbar from "./navbar.vue";

const meta: Meta<typeof FioNavbar> = {
    title: "Components/FioNavbar",
    component: FioNavbar,
    tags: ["autodocs"],
    parameters: {
        layout: "fullscreen",
    },
};

export default meta;
type Story = StoryObj<typeof FioNavbar>;

export const Default: Story = {
    render: () => ({
        template: `
            <fio-navbar>
                <template #nav-brand>
                    <a href="#">MyApp</a>
                </template>
                <template #nav-left>
                    <li><a href="#">Home</a></li>
                    <li><a href="#">About</a></li>
                </template>
                <template #nav-right>
                    <li><a href="#">Login</a></li>
                </template>
            </fio-navbar>
        `,
    }),
};

export const BrandOnly: Story = {
    render: () => ({
        template: `
            <fio-navbar>
                <template #nav-brand>
                    <a href="#">MyApp</a>
                </template>
            </fio-navbar>
        `,
    }),
};

export const WithIcons: Story = {
    name: "With Icons",
    render: () => ({
        template: `
            <fio-navbar>
                <template #nav-brand>
                    <a href="#">
                        <fio-icon icon="flag" />
                        MyApp
                    </a>
                </template>
                <template #nav-left>
                    <li><a href="#"><fio-icon icon="home" /> Home</a></li>
                    <li><a href="#"><fio-icon icon="gear" /> Settings</a></li>
                </template>
                <template #nav-right>
                    <li><a href="#"><fio-icon icon="right-to-bracket" /> Login</a></li>
                </template>
            </fio-navbar>
        `,
    }),
};
