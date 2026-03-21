import type { Meta, StoryObj } from "@storybook/vue3";
import FioImage from "./fio-image.vue";
import { IMAGES } from "@/model/images";

const meta: Meta<typeof FioImage> = {
  title: "Components/FioImage",
  component: FioImage,
  tags: ["autodocs"],
  argTypes: {
    name: {
      control: "select",
      options: Object.keys(IMAGES),
      description: "Name of the image to display",
    },
    alt: {
      control: "text",
      description:
        "Alternative text for accessibility (omit for decorative images)",
    },
  },
};

export default meta;
type Story = StoryObj<typeof FioImage>;

export const Logo: Story = {
  args: { name: "logo", alt: "Logo" },
};

export const Spring: Story = {
  args: { name: "spring", alt: "Spring" },
};

export const Kotlin: Story = {
  args: { name: "kotlin", alt: "Kotlin" },
};

export const Vue: Story = {
  args: { name: "vue", alt: "Vue" },
};

export const Typescript: Story = {
  args: { name: "typescript", alt: "TypeScript" },
};

export const Docker: Story = {
  args: { name: "docker", alt: "Docker" },
};

export const Kubernetes: Story = {
  args: { name: "kubernetes", alt: "Kubernetes" },
};

export const Helm: Story = {
  args: { name: "helm", alt: "Helm" },
};

export const AllImages: Story = {
  name: "All Available Images",
  render: () => ({
    template: `
            <div style="display: flex; gap: 1.5rem; flex-wrap: wrap; align-items: flex-start;">
                <div v-for="name in images" :key="name" style="display: flex; flex-direction: column; align-items: center; gap: 0.5rem; width: 80px;">
                    <fio-image :name="name" style="width: 60px;" />
                    <span style="font-size: 0.6rem; color: #666; text-align: center; word-break: break-all;">{{ name }}</span>
                </div>
            </div>
        `,
    data() {
      return {
        images: Object.keys(IMAGES),
      };
    },
    setup() {
      return { IMAGES };
    },
  }),
};
