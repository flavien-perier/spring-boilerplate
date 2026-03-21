import type { Meta, StoryObj } from "@storybook/vue3";
import { ref } from "vue";
import FioSideNavLayout from "./side-nav.layout.vue";
import type { NavbarElement } from "@/model/navbar-element";

const meta: Meta<typeof FioSideNavLayout> = {
  title: "Layouts/FioSideNavLayout",
  component: FioSideNavLayout,
  tags: ["autodocs"],
  parameters: {
    layout: "fullscreen",
  },
};

export default meta;
type Story = StoryObj<typeof FioSideNavLayout>;

const defaultElements: NavbarElement[] = [
  {
    i18nKey: "fio.layout.home",
    direction: "start",
    faIcon: "home",
    action: () => {},
  },
  {
    i18nKey: "fio.layout.settings",
    direction: "start",
    faIcon: "gear",
    action: () => {},
  },
  {
    i18nKey: "fio.layout.users",
    direction: "start",
    faIcon: "user",
    action: () => {},
  },
  {
    i18nKey: "fio.layout.logout",
    direction: "end",
    faIcon: "right-to-bracket",
    action: () => {},
  },
];

export const Default: Story = {
  render: () => ({
    setup() {
      return { elements: defaultElements };
    },
    template: `
            <fio-side-nav-layout :elements="elements">
                <template #brand>
                    <a href="#"><fio-icon icon="flag" /> MyApp</a>
                </template>
                <div style="padding: 2rem;">
                    <h1>Page content</h1>
                    <p>Main content area goes here. The sidebar can be collapsed with the arrow button.</p>
                </div>
            </fio-side-nav-layout>
        `,
  }),
};

export const Interactive: Story = {
  render: () => ({
    setup() {
      const activeItem = ref<string | null>(null);
      const elements: NavbarElement[] = [
        {
          i18nKey: "fio.layout.home",
          direction: "start",
          faIcon: "home",
          action: () => {
            activeItem.value = "home";
          },
        },
        {
          i18nKey: "fio.layout.settings",
          direction: "start",
          faIcon: "gear",
          action: () => {
            activeItem.value = "settings";
          },
        },
        {
          i18nKey: "fio.layout.users",
          direction: "start",
          faIcon: "user",
          action: () => {
            activeItem.value = "users";
          },
        },
        {
          i18nKey: "fio.layout.logout",
          direction: "end",
          faIcon: "right-to-bracket",
          action: () => {
            activeItem.value = "logout";
          },
        },
      ];
      return { elements, activeItem };
    },
    template: `
            <fio-side-nav-layout :elements="elements">
                <template #brand>
                    <a href="#"><fio-icon icon="flag" /> MyApp</a>
                </template>
                <div style="padding: 2rem;">
                    <p v-if="activeItem">Active section: <strong>{{ activeItem }}</strong></p>
                    <p v-else>Click a sidebar item to navigate.</p>
                </div>
            </fio-side-nav-layout>
        `,
  }),
};
