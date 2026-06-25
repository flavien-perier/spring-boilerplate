import "./styles/index.scss";
import Icon from "./components/icon.vue";
import Image from "./components/image.vue";
import QrCode from "./components/qr-code.vue";
import InputCreatePassword from "./components/inputs/input-create-password.vue";
import InputPassword from "./components/inputs/input-password.vue";
import InputEmail from "./components/inputs/input-email.vue";
import Button from "./components/inputs/input-button.vue";
import Modal from "./components/modal.vue";
import Alert from "./components/alert.vue";
import Table from "./components/table.vue";
import InputText from "./components/inputs/input-text.vue";
import InputCheckbox from "./components/inputs/input-checkbox.vue";
import InputToggle from "./components/inputs/input-toggle.vue";
import InputTriToggle from "./components/inputs/input-tri-toggle.vue";
import InputSelect from "./components/inputs/input-select.vue";
import Markdown from "./components/markdown.vue";
import InputMarkdown from "./components/inputs/input-markdown.vue";
import TopNavLayout from "./layouts/top-nav.layout.vue";
import SideNavLayout from "./layouts/side-nav.layout.vue";
import AuthLayout from "./layouts/auth.layout.vue";
import TabLayout from "./layouts/tab.layout.vue";
import PageView from "./layouts/page-view.layout.vue";
import SplitLayout from "./layouts/split.layout.vue";

import { passwordUtil } from "@/utils/password-util";
import { downloadUtil } from "@/utils/download-util";
import { markdownUtil } from "@/utils/markdown-util";

import type { App } from "vue";

export default {
  install: (app: App) => {
    app.component("fio-icon", Icon);
    app.component("fio-image", Image);
    app.component("fio-qr-code", QrCode);
    app.component("fio-input-create-password", InputCreatePassword);
    app.component("fio-input-password", InputPassword);
    app.component("fio-input-email", InputEmail);
    app.component("fio-input-button", Button);
    app.component("fio-modal", Modal);
    app.component("fio-alert", Alert);
    app.component("fio-table", Table);
    app.component("fio-input-text", InputText);
    app.component("fio-input-checkbox", InputCheckbox);
    app.component("fio-input-toggle", InputToggle);
    app.component("fio-input-tri-toggle", InputTriToggle);
    app.component("fio-input-select", InputSelect);
    app.component("fio-markdown", Markdown);
    app.component("fio-input-markdown", InputMarkdown);
    app.component("fio-top-nav-layout", TopNavLayout);
    app.component("fio-side-nav-layout", SideNavLayout);
    app.component("fio-auth-layout", AuthLayout);
    app.component("fio-tab-layout", TabLayout);
    app.component("fio-page-view", PageView);
    app.component("fio-split-layout", SplitLayout);
  },
};

export { passwordUtil, downloadUtil, markdownUtil };

export type { MarkdownOptions } from "@/utils/markdown-util";

export type {
  Icon,
  Image,
  QrCode,
  InputCreatePassword,
  InputPassword,
  InputEmail,
  Button,
  Modal,
  Alert,
  Table,
  InputText,
  InputCheckbox,
  InputToggle,
  InputTriToggle,
  InputSelect,
  Markdown,
  InputMarkdown,
  TopNavLayout,
  SideNavLayout,
  AuthLayout,
  TabLayout,
  PageView,
  SplitLayout,
};

export type { TabElement } from "./model/tab-element";
export type { SelectOption } from "./components/inputs/input-select.vue";
export type { NavbarElement } from "./model/navbar-element";
export type { IconColor } from "./model/icon-color";
export type { IconSize } from "./model/icon-size";
export type { ImageName } from "./model/images";
export type {
  TableHeader,
  SortDirection,
  TableSortEvent,
  TablePageEvent,
  TablePageSizeEvent,
} from "./model/table-header";
