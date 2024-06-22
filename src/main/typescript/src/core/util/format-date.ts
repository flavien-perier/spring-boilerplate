import {useI18n} from "vue-i18n";

export function formatDate(value: string | Date): string {
  let date = typeof value === "string" ? new Date(value) : value;

  return useI18n().d(date, "long");
}