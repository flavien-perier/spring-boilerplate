import FioIcon from "./components/fio-icon.vue";
import InputCreatePassword from "./components/inputs/input-create-password.vue";
import InputPassword from "./components/inputs/input-password.vue";
import InputEmail from "./components/inputs/input-email.vue";
import Button from "./components/inputs/input-button.vue";
import Navbar from "./components/navbar.vue";
import Footer from "./components/footer.vue";

import { passwordUtil } from "@/utils/password-util";

import type {App} from "vue";

export default {
  install: (app: App) => {
    app.component("fio-icon", FioIcon);
    app.component("fio-input-create-password", InputCreatePassword);
    app.component("fio-input-password", InputPassword);
    app.component("fio-input-email", InputEmail);
    app.component("fio-input-button", Button);
    app.component("fio-navbar", Navbar);
    app.component("fio-footer", Footer);
  },
};

export type {
  FioIcon,
  InputCreatePassword,
  InputPassword,
  InputEmail,
  Button,
  Navbar,
  Footer,
}

export { passwordUtil };
