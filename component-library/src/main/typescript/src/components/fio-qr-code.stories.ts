import type { Meta, StoryObj } from "@storybook/vue3";
import FioQrCode from "./fio-qr-code.vue";

const meta: Meta<typeof FioQrCode> = {
  title: "Components/FioQrCode",
  component: FioQrCode,
  tags: ["autodocs"],
  argTypes: {
    value: {
      control: "text",
      description: "The string to encode as a QR code",
    },
  },
};

export default meta;
type Story = StoryObj<typeof FioQrCode>;

export const Url: Story = {
  args: { value: "https://example.com" },
};

export const PlainText: Story = {
  args: { value: "Hello, World!" },
};

export const LongUrl: Story = {
  name: "Long URL",
  args: {
    value:
      "https://www.example.com/path/to/some/resource?param1=value1&param2=value2&param3=value3",
  },
};
