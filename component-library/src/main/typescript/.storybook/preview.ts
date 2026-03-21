import type { Preview, App } from "@storybook/vue3";
import { setup } from "@storybook/vue3";
import { createI18n } from "vue-i18n";
import ComponentLibrary from "../src/index";
import "../src/styles/index.scss";

const i18n = createI18n({
    legacy: false,
    locale: "en",
    messages: {
        en: {
            "fio.email": "Email address",
            "fio.password": "Password",
            "fio.repeat-password": "Confirm password",
            "fio.password.constraint.length": "At least {minPasswordLength} characters",
            "fio.password.constraint.lowercase": "At least one lowercase letter",
            "fio.password.constraint.uppercase": "At least one uppercase letter",
            "fio.password.constraint.number": "At least one number",
            "fio.password.constraint.special-character": "At least one special character",
        },
        fr: {
            "fio.email": "Adresse e-mail",
            "fio.password": "Mot de passe",
            "fio.repeat-password": "Confirmer le mot de passe",
            "fio.password.constraint.length": "Au moins {minPasswordLength} caractères",
            "fio.password.constraint.lowercase": "Au moins une lettre minuscule",
            "fio.password.constraint.uppercase": "Au moins une lettre majuscule",
            "fio.password.constraint.number": "Au moins un chiffre",
            "fio.password.constraint.special-character": "Au moins un caractère spécial",
        },
    },
});

setup((app: App) => {
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
