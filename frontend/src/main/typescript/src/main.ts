import { createApp, markRaw } from "vue";
import { createPinia } from "pinia";
import "@generated/component-library/style.css";
import componentLibrary from "@generated/component-library";

import App from "@/core/application.view.vue";
import i18n from "./i18n";
import router from "./router";

const pinia = createPinia();
pinia.use(({ store }) => {
  store.$router = markRaw(router);
  store.$i18n = i18n.global;
});

createApp(App)
  .use(pinia)
  .use(router)
  .use(i18n)
  .use(componentLibrary)
  .mount("#app");
