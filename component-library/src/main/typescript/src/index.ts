import "./styles/index.scss";
import FioIcon from "./components/fio-icon.vue";
import FioImage from "./components/fio-image.vue";
import FioQrCode from "./components/fio-qr-code.vue";
import InputCreatePassword from "./components/inputs/input-create-password.vue";
import InputPassword from "./components/inputs/input-password.vue";
import InputEmail from "./components/inputs/input-email.vue";
import Button from "./components/inputs/input-button.vue";
import Modal from "./components/modal.vue";
import Alert from "./components/alert.vue";
import Table from "./components/table.vue";
import InputText from "./components/inputs/input-text.vue";
import TopNavLayout from "./layouts/top-nav.layout.vue";
import SideNavLayout from "./layouts/side-nav.layout.vue";
import AuthLayout from "./layouts/auth.layout.vue";
import TabLayout from "./layouts/tab.layout.vue";
import PageView from "./layouts/page-view.layout.vue";
import SplitLayout from "./layouts/split.layout.vue";

import { passwordUtil } from "@/utils/password-util";

import type { App } from "vue";

export default {
  install: (app: App) => {
    app.component("fio-icon", FioIcon);
    app.component("fio-image", FioImage);
    app.component("fio-qr-code", FioQrCode);
    app.component("fio-input-create-password", InputCreatePassword);
    app.component("fio-input-password", InputPassword);
    app.component("fio-input-email", InputEmail);
    app.component("fio-input-button", Button);
    app.component("fio-modal", Modal);
    app.component("fio-alert", Alert);
    app.component("fio-table", Table);
    app.component("fio-input-text", InputText);
    app.component("fio-top-nav-layout", TopNavLayout);
    app.component("fio-side-nav-layout", SideNavLayout);
    app.component("fio-auth-layout", AuthLayout);
    app.component("fio-tab-layout", TabLayout);
    app.component("fio-page-view", PageView);
    app.component("fio-split-layout", SplitLayout);
  },
};

export { passwordUtil };

export type {
  FioIcon,
  FioImage,
  FioQrCode,
  InputCreatePassword,
  InputPassword,
  InputEmail,
  Button,
  Modal,
  Alert,
  Table,
  InputText,
  TopNavLayout,
  SideNavLayout,
  AuthLayout,
  TabLayout,
  PageView,
  SplitLayout,
};

export type { TabElement } from "./model/tab-element";
export type { NavbarElement } from "./model/navbar-element";
export type { IconColor } from "./model/icon-color";
export type { IconSize } from "./model/icon-size";
export type { ImageName } from "./model/images";
export type { TableHeader } from "./model/table-header";
