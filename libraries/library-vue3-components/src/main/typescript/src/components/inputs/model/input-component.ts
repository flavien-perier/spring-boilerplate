/**
 * Minimal contract every value-carrying input component must expose.
 *
 * `input-button` is intentionally excluded: it is an action component that
 * emits `click` and carries no value.
 */
export interface InputComponent<T = unknown> {
  modelValue: T;
  disabled?: boolean;
}
