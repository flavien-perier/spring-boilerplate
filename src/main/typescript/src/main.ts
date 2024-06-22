import { createApp, markRaw } from "vue";
import { createPinia } from "pinia";
import "@/assets/scss/index.scss";
import {
  faHome,
  faUser,
  faFlag,
  faGear,
  faTrash,
  faSpinner,
  faRightToBracket,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";

import App from "@/core/application.view.vue";
import i18n from "./i18n";
import router from "./router";

library.add(
  faHome,
  faUser,
  faFlag,
  faGear,
  faTrash,
  faSpinner,
  faRightToBracket,
);

const pinia = createPinia();
pinia.use(({ store }) => {
  store.$router = markRaw(router);
  store.$i18n = i18n.global;
});

createApp(App)
  .component("font-awesome-icon", FontAwesomeIcon)
  .use(pinia)
  .use(router)
  .use(i18n)
  .mount("#app");
