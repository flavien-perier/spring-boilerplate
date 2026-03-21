import type { Meta, StoryObj } from "@storybook/vue3";
import { ref } from "vue";
import FioInputCreatePassword from "./input-create-password.vue";

const meta: Meta<typeof FioInputCreatePassword> = {
    title: "Components/Inputs/FioInputCreatePassword",
    component: FioInputCreatePassword,
    tags: ["autodocs"],
    argTypes: {
        modelValue: {
            control: "text",
            description: "Current password value (v-model)",
        },
        minPasswordLength: {
            control: { type: "number", min: 4, max: 32, step: 1 },
            description: "Minimum required password length (default: 8)",
        },
        "onUpdate:modelValue": { action: "update:modelValue" },
        "onUpdate:isValid": { action: "update:isValid" },
    },
};

export default meta;
type Story = StoryObj<typeof FioInputCreatePassword>;

export const Default: Story = {
    args: {
        modelValue: "",
        minPasswordLength: 8,
    },
};

export const LongerMinLength: Story = {
    name: "Longer Min Length (12 chars)",
    args: {
        modelValue: "",
        minPasswordLength: 12,
    },
};

export const Interactive: Story = {
    name: "Interactive (v-model + validity)",
    render: () => ({
        setup() {
            const password = ref("");
            const isValid = ref(false);
            return { password, isValid };
        },
        template: `
            <div style="width: 360px;">
                <fio-input-create-password
                    v-model="password"
                    v-model:isValid="isValid"
                    :min-password-length="8"
                />
                <div style="margin-top: 16px; font-size: 0.85rem; color: #666; border-top: 1px solid #eee; padding-top: 8px;">
                    Password: <code>{{ password || "(empty)" }}</code><br />
                    Valid: <code :style="{ color: isValid ? 'green' : 'red' }">{{ isValid }}</code>
                </div>
            </div>
        `,
    }),
};

export const WithValidPassword: Story = {
    name: "Pre-filled Valid Password",
    render: () => ({
        setup() {
            const password = ref("Str0ng!Pass");
            const isValid = ref(false);
            return { password, isValid };
        },
        template: `
            <div style="width: 360px;">
                <fio-input-create-password
                    v-model="password"
                    v-model:isValid="isValid"
                    :min-password-length="8"
                />
                <div style="margin-top: 16px; font-size: 0.85rem; color: #666; border-top: 1px solid #eee; padding-top: 8px;">
                    Password: <code>{{ password }}</code><br />
                    Valid: <code :style="{ color: isValid ? 'green' : 'red' }">{{ isValid }}</code>
                    <br />
                    <small>(Enter the same password in the confirm field to mark as valid)</small>
                </div>
            </div>
        `,
    }),
};
