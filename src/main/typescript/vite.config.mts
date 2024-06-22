import { fileURLToPath, URL } from "url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";

export default defineConfig({
  plugins: [vue()],
  build: {
    target: "es2019",
  },
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  server: {
    base: "/vue-user.html",
    proxy: {
      "/api": {
        changeOrigin: true,
        target:  "http://127.0.0.1:8080/",
      },
    },
  },
});
