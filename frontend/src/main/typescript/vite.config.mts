import { fileURLToPath } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
  plugins: [vue()],
  build: {
    target: "es2019",
  },
  resolve: {
    alias: [
      { find: "@", replacement: fileURLToPath(new URL("./src", import.meta.url)) },
      { find: "@generated/component-library/style.css", replacement: fileURLToPath(new URL("./generated/component-library/component-library.es.css", import.meta.url)) },
      { find: "@generated/component-library", replacement: fileURLToPath(new URL("./generated/component-library/component-library.es.js", import.meta.url)) },
      { find: "@generated/api", replacement: fileURLToPath(new URL("./generated/api", import.meta.url)) },
      { find: "@generated/utils", replacement: fileURLToPath(new URL("./generated/utils", import.meta.url)) },
    ],
  },
  server: {
    proxy: {
      "/api": {
        changeOrigin: true,
        target:  "http://127.0.0.1:8080/",
      },
    },
  },
});
