import type { Meta, StoryObj } from "@storybook/vue3";
import { ref, computed } from "vue";
import FioPageView from "./page-view.layout.vue";
import type { TabElement } from "@/model/tab-element";

const meta: Meta<typeof FioPageView> = {
  title: "Layouts/FioPageView",
  component: FioPageView,
  tags: ["autodocs"],
  parameters: {
    layout: "fullscreen",
  },
  argTypes: {
    title: { control: "text", description: "Page heading text" },
  },
};

export default meta;
type Story = StoryObj<typeof FioPageView>;

export const Default: Story = {
  render: () => ({
    template: `
            <fio-page-view title="My Page">
                <p>This is the page content. Resize the viewport to observe the responsive max-width behaviour.</p>
                <p style="margin-top: 1rem;">At 768 px the container is capped at 768 px, at 992 px it grows to 992 px, at 1200 px to 1200 px, and at 1400 px it reaches the maximum width of 1400 px.</p>
            </fio-page-view>
        `,
  }),
};

export const WithForm: Story = {
  render: () => ({
    template: `
            <fio-page-view title="Account settings">
                <fio-input-email model-value="" />
                <fio-input-create-password model-value="" style="margin-top: 1rem;" />
                <fio-input-button label="Save changes" style="margin-top: 1rem;" />
            </fio-page-view>
        `,
  }),
};

export const WithTabs: Story = {
  render: () => ({
    setup() {
      const activeTab = ref(0);
      const tabs = computed<TabElement[]>(() => [
        {
          i18nKey: "fio.layout.tab-one",
          isActive: activeTab.value === 0,
          action: () => {
            activeTab.value = 0;
          },
        },
        {
          i18nKey: "fio.layout.tab-two",
          isActive: activeTab.value === 1,
          action: () => {
            activeTab.value = 1;
          },
        },
      ]);
      return { activeTab, tabs };
    },
    template: `
            <fio-page-view title="Administration">
                <fio-tab-layout :tabs="tabs">
                    <div style="padding: 1rem 0;">
                        <p v-if="activeTab === 0">Content of first tab</p>
                        <p v-else>Content of second tab</p>
                    </div>
                </fio-tab-layout>
            </fio-page-view>
        `,
  }),
};
