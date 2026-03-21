export type NotificationType = "info" | "danger";

export interface Notification {
  id: number;
  message: string;
  type: NotificationType;
}
