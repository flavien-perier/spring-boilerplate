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
    title: {
      control: "text",
      description: "Optional heading displayed above the slot content",
    },
  },
};

export default meta;
type Story = StoryObj<typeof FioAuthLayout>;

export const Login: Story = {
  render: () => ({
    template: `
            <fio-auth-layout name="undraw_login" title="Sign in">
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
            <fio-auth-layout name="undraw_create_account" title="Create account">
                <fio-input-email model-value="" class="mb-l" />
                <fio-input-button label="Create" />
            </fio-auth-layout>
        `,
  }),
};

export const ForgotPassword: Story = {
  render: () => ({
    template: `
            <fio-auth-layout name="undraw_forgot_password" title="Forgot password">
                <fio-input-email model-value="" class="mb-l" />
                <fio-input-button label="Send" />
            </fio-auth-layout>
        `,
  }),
};
