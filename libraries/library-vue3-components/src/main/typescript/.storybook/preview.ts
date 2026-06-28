import type { Preview, App, Decorator } from "@storybook/vue3";
import { setup } from "@storybook/vue3";
import { createI18n } from "vue-i18n";
import { createRouter, createMemoryHistory } from "vue-router";
import ComponentLibrary from "../src/index";
import "../src/styles/index.scss";
import en from "./i18n/en.json";
import fr from "./i18n/fr.json";

const i18n = createI18n({
  locale: "en",
  messages: { en, fr },
});

const router = createRouter({
  history: createMemoryHistory(),
  routes: [
    { name: "tab-one", path: "/tab-one", component: { template: "<div/>" } },
    { name: "tab-two", path: "/tab-two", component: { template: "<div/>" } },
    {
      name: "tab-three",
      path: "/tab-three",
      component: { template: "<div/>" },
    },
    { name: "tab-four", path: "/tab-four", component: { template: "<div/>" } },
  ],
});

setup((app: App) => {
  app.use(router);
  app.use(i18n);
  app.use(ComponentLibrary);
});

export const globalTypes = {
  theme: {
    name: "Theme",
    description: "Color theme",
    defaultValue: "light",
    toolbar: {
      icon: "paintbrush",
      items: [
        { value: "light", title: "Light", icon: "sun" },
        { value: "dark", title: "Dark", icon: "moon" },
      ],
      dynamicTitle: true,
    },
  },
};

const themeDecorator: Decorator = (story, context) => {
  document.documentElement.setAttribute(
    "data-theme",
    context.globals["theme"] ?? "light"
  );
  return story();
};

const preview: Preview = {
  decorators: [themeDecorator],
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
    layout: "centered",
  },
};

export default preview;
