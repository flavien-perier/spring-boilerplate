export type NotificationType = "info" | "alert"

export interface Notification {
  id: number;
  message: string;
  type: NotificationType;
}