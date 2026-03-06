import { createI18n } from "vue-i18n";
import fr from "@/locales/fr.json";
import en from "@/locales/en.json";

import datetimeFormatFr from "@/locales/datetime-format/fr.json";
import datetimeFormatEn from "@/locales/datetime-format/en.json";

const i18n = createI18n({
  legacy: false,
  locale: "fr",
  fallbackLocale: "fr",
  allowComposition: true,
  datetimeFormats: {
    fr: datetimeFormatFr,
    en: datetimeFormatEn,
  } as any,
  messages: {
    fr,
    en,
  } as any,
});

export default i18n;
