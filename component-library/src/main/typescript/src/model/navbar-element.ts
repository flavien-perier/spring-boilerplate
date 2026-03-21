export interface NavbarElement {
  i18nKey: string;
  action: () => void;
  direction: "start" | "end";
  faIcon: string;
}
