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
            description: 'Variant: "submit" renders a full-width primary button, any other value renders a danger button',
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

export const AllVariants: Story = {
    name: "All Variants",
    render: () => ({
        template: `
            <div style="display: flex; flex-direction: column; gap: 1rem; width: 300px;">
                <fio-input-button label="Submit Button" variant="submit" />
                <fio-input-button label="Submit (Disabled)" variant="submit" :disabled="true" />
                <fio-input-button label="Danger Button" variant="danger" />
                <fio-input-button label="Danger (Disabled)" variant="danger" :disabled="true" />
            </div>
        `,
    }),
};
