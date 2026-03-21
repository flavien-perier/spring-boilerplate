import type { Preview, App } from "@storybook/vue3";
import { setup } from "@storybook/vue3";
import { createI18n } from "vue-i18n";
import { createRouter, createMemoryHistory } from "vue-router";
import ComponentLibrary from "../src/index";
import "../src/styles/index.scss";

const i18n = createI18n({
  locale: "en",
  messages: {
    en: {
      "fio.email": "Email address",
      "fio.password": "Password",
      "fio.repeat-password": "Confirm password",
      "fio.password.constraint.length":
        "At least {minPasswordLength} characters",
      "fio.password.constraint.lowercase": "At least one lowercase letter",
      "fio.password.constraint.uppercase": "At least one uppercase letter",
      "fio.password.constraint.number": "At least one number",
      "fio.password.constraint.special-character":
        "At least one special character",
      "fio.layout.home": "Home",
      "fio.layout.settings": "Settings",
      "fio.layout.login": "Login",
      "fio.layout.logout": "Logout",
      "fio.layout.account": "Account",
      "fio.layout.users": "Users",
      "fio.layout.tab-one": "Tab One",
      "fio.layout.tab-two": "Tab Two",
      "fio.layout.tab-three": "Tab Three",
    },
    fr: {
      "fio.email": "Adresse e-mail",
      "fio.password": "Mot de passe",
      "fio.repeat-password": "Confirmer le mot de passe",
      "fio.password.constraint.length":
        "Au moins {minPasswordLength} caractères",
      "fio.password.constraint.lowercase": "Au moins une lettre minuscule",
      "fio.password.constraint.uppercase": "Au moins une lettre majuscule",
      "fio.password.constraint.number": "Au moins un chiffre",
      "fio.password.constraint.special-character":
        "Au moins un caractère spécial",
      "fio.layout.home": "Accueil",
      "fio.layout.settings": "Paramètres",
      "fio.layout.login": "Connexion",
      "fio.layout.logout": "Déconnexion",
      "fio.layout.account": "Compte",
      "fio.layout.users": "Utilisateurs",
      "fio.layout.tab-one": "Onglet un",
      "fio.layout.tab-two": "Onglet deux",
      "fio.layout.tab-three": "Onglet trois",
    },
  },
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

const preview: Preview = {
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
