import type { Meta, StoryObj } from "@storybook/vue3";
import { ref } from "vue";
import FioInputPassword from "./input-password.vue";

const meta: Meta<typeof FioInputPassword> = {
    title: "Components/Inputs/FioInputPassword",
    component: FioInputPassword,
    tags: ["autodocs"],
    argTypes: {
        modelValue: {
            control: "text",
            description: "Current password value (v-model)",
        },
        label: {
            control: "text",
            description: "Label text displayed above the input",
        },
        isValid: {
            control: "boolean",
            description: "Controls whether the invalid CSS class is applied (validation is driven externally)",
        },
        "onUpdate:modelValue": { action: "update:modelValue" },
        onInput: { action: "input" },
        onFocus: { action: "focus" },
        onBlur: { action: "blur" },
    },
};

export default meta;
type Story = StoryObj<typeof FioInputPassword>;

export const Default: Story = {
    args: {
        modelValue: "",
        label: "Password",
        isValid: true,
    },
};

export const WithValue: Story = {
    name: "With Value",
    args: {
        modelValue: "mySecretPassword123!",
        label: "Password",
        isValid: true,
    },
};

export const Invalid: Story = {
    name: "Invalid State",
    args: {
        modelValue: "weak",
        label: "Password",
        isValid: false,
    },
};

export const CustomLabel: Story = {
    name: "Custom Label",
    args: {
        modelValue: "",
        label: "Confirm Password",
        isValid: true,
    },
};

export const Interactive: Story = {
    name: "Interactive (toggle visibility)",
    render: () => ({
        setup() {
            const password = ref("");
            const isValid = ref(true);
            return { password, isValid };
        },
        template: `
            <div style="width: 320px;">
                <div class="form-outline mb-2">
                    <fio-input-password
                        v-model="password"
                        label="Password"
                        :is-valid="isValid"
                    />
                </div>
                <div style="margin-top: 8px; font-size: 0.85rem; color: #666;">
                    Value: <code>{{ password || "(empty)" }}</code>
                </div>
                <div class="form-check mt-2">
                    <input class="form-check-input" type="checkbox" id="validCheck" v-model="isValid" />
                    <label class="form-check-label" for="validCheck">Is valid</label>
                </div>
            </div>
        `,
    }),
};
