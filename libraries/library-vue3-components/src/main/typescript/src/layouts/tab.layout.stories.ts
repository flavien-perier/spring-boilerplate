import type { Meta, StoryObj } from "@storybook/vue3";
import { ref, computed } from "vue";
import FioTabLayout from "./tab.layout.vue";
import type { TabElement } from "@/model/tab-element";

const meta: Meta<typeof FioTabLayout> = {
  title: "Layouts/FioTabLayout",
  component: FioTabLayout,
  tags: ["autodocs"],
};

export default meta;
type Story = StoryObj<typeof FioTabLayout>;

export const Default: Story = {
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
        {
          i18nKey: "fio.layout.tab-three",
          isActive: activeTab.value === 2,
          action: () => {
            activeTab.value = 2;
          },
        },
      ]);
      return { activeTab, tabs };
    },
    template: `
            <div style="padding: 1rem;">
                <fio-tab-layout :tabs="tabs">
                    <div style="padding: 1rem 0;">
                        <p v-if="activeTab === 0">Content of Tab One</p>
                        <p v-else-if="activeTab === 1">Content of Tab Two</p>
                        <p v-else>Content of Tab Three</p>
                    </div>
                </fio-tab-layout>
            </div>
        `,
  }),
};

export const TwoTabs: Story = {
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
            <div style="padding: 1rem;">
                <fio-tab-layout :tabs="tabs">
                    <div style="padding: 1rem 0;">
                        <p v-if="activeTab === 0">First tab content</p>
                        <p v-else>Second tab content</p>
                    </div>
                </fio-tab-layout>
            </div>
        `,
  }),
};
