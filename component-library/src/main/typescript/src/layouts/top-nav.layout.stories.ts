import type { Meta, StoryObj } from "@storybook/vue3";
import { ref } from "vue";
import FioTopNavLayout from "./top-nav.layout.vue";
import type { NavbarElement } from "./types";

const meta: Meta<typeof FioTopNavLayout> = {
  title: "Layouts/FioTopNavLayout",
  component: FioTopNavLayout,
  tags: ["autodocs"],
  parameters: {
    layout: "fullscreen",
  },
};

export default meta;
type Story = StoryObj<typeof FioTopNavLayout>;

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
    i18nKey: "fio.layout.login",
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
            <fio-top-nav-layout
                :elements="elements"
                footer-href="https://example.com"
                footer-label="example.com"
            >
                <template #brand>
                    <a href="#"><fio-icon icon="flag" /> MyApp</a>
                </template>
                <div style="padding: 2rem;">
                    <h1>Page content</h1>
                    <p>Main content area goes here.</p>
                </div>
            </fio-top-nav-layout>
        `,
  }),
};

export const Authenticated: Story = {
  render: () => ({
    setup() {
      const elements: NavbarElement[] = [
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
          i18nKey: "fio.layout.account",
          direction: "end",
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
      return { elements };
    },
    template: `
            <fio-top-nav-layout
                :elements="elements"
                footer-href="https://example.com"
                footer-label="example.com"
            >
                <template #brand>
                    <a href="#"><fio-icon icon="flag" /> MyApp</a>
                </template>
                <div style="padding: 2rem;">
                    <h1>Dashboard</h1>
                    <p>Authenticated user content area.</p>
                </div>
            </fio-top-nav-layout>
        `,
  }),
};

export const Interactive: Story = {
  render: () => ({
    setup() {
      const clicked = ref<string | null>(null);
      const elements: NavbarElement[] = [
        {
          i18nKey: "fio.layout.home",
          direction: "start",
          faIcon: "home",
          action: () => {
            clicked.value = "home";
          },
        },
        {
          i18nKey: "fio.layout.settings",
          direction: "start",
          faIcon: "gear",
          action: () => {
            clicked.value = "settings";
          },
        },
        {
          i18nKey: "fio.layout.login",
          direction: "end",
          faIcon: "right-to-bracket",
          action: () => {
            clicked.value = "login";
          },
        },
      ];
      return { elements, clicked };
    },
    template: `
            <fio-top-nav-layout
                :elements="elements"
                footer-href="https://example.com"
                footer-label="example.com"
            >
                <template #brand>
                    <a href="#"><fio-icon icon="flag" /> MyApp</a>
                </template>
                <div style="padding: 2rem;">
                    <p v-if="clicked">Last clicked: <strong>{{ clicked }}</strong></p>
                    <p v-else>Click a nav item above.</p>
                </div>
            </fio-top-nav-layout>
        `,
  }),
};
