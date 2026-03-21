import type { StorybookConfig } from "@storybook/vue3-vite";
import { fileURLToPath, URL } from "url";

const config: StorybookConfig = {
    stories: ["../src/**/*.stories.@(js|jsx|mjs|ts|tsx)"],
    addons: [
        "@storybook/addon-links",
        "@storybook/addon-essentials",
        "@storybook/addon-interactions",
    ],
    framework: {
        name: "@storybook/vue3-vite",
        options: {},
    },
    async viteFinal(config) {
        const { mergeConfig } = await import("vite");
        return mergeConfig(config, {
            resolve: {
                alias: {
                    "@": fileURLToPath(new URL("../src", import.meta.url)),
                },
            },
        });
    },
};

export default config;
