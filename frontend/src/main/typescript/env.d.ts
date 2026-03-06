/// <reference types="vite/client" />

import "pinia";

declare module "pinia" {
  import type Router from "vue-router";
  export interface PiniaCustomProperties {
    $router: Router;
    $i18n: { t: (string) => string };
  }
}
