import type { Meta, StoryObj } from "@storybook/vue3";
import FioAuthLayout from "./auth.layout.vue";

const meta: Meta<typeof FioAuthLayout> = {
  title: "Layouts/FioAuthLayout",
  component: FioAuthLayout,
  tags: ["autodocs"],
  parameters: {
    layout: "fullscreen",
  },
  argTypes: {
    name: { control: "text", description: "Name of the illustration image" },
  },
};

export default meta;
type Story = StoryObj<typeof FioAuthLayout>;

export const Login: Story = {
  render: () => ({
    template: `
            <fio-auth-layout name="undraw_login">
                <h1 style="margin-bottom: 1rem; text-align: center;">Sign in</h1>
                <fio-input-email model-value="" class="mb-l" />
                <fio-input-password label="Password" model-value="" class="mb-l" />
                <fio-input-button label="Connect" />
            </fio-auth-layout>
        `,
  }),
};

export const CreateAccount: Story = {
  render: () => ({
    template: `
            <fio-auth-layout name="undraw_create_account">
                <h1 style="margin-bottom: 1rem; text-align: center;">Create account</h1>
                <fio-input-email model-value="" class="mb-l" />
                <fio-input-button label="Create" />
            </fio-auth-layout>
        `,
  }),
};

export const ForgotPassword: Story = {
  render: () => ({
    template: `
            <fio-auth-layout name="undraw_forgot_password">
                <h1 style="margin-bottom: 1rem; text-align: center;">Forgot password</h1>
                <fio-input-email model-value="" class="mb-l" />
                <fio-input-button label="Send" />
            </fio-auth-layout>
        `,
  }),
};
