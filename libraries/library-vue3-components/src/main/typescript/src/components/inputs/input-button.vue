<template>
  <button
    type="button"
    class="fio-btn"
    :class="[`fio-btn--${variant}`, size && `fio-btn--${size}`]"
    :disabled="disabled || waiting"
    @click="emit('click')"
  >
    <fio-icon
      :icon="waiting ? 'spinner' : icon"
      class="fio-btn__icon"
      v-if="waiting || icon"
    />
    <span v-if="label" class="fio-btn__label">{{ label }}</span>
  </button>
</template>

<script setup lang="ts">
import FioIcon from "../icon.vue";
import type { InputSize } from "@/model/input-size";

defineOptions({
  name: "FioInputButton",
});

const {
  label,
  disabled = false,
  waiting = false,
  variant = "submit",
  size,
  icon,
} = defineProps<{
  label?: string;
  disabled?: boolean;
  waiting?: boolean;
  variant?: "submit" | "warning" | "danger" | "ghost";
  size?: InputSize;
  icon?: string;
}>();

const emit = defineEmits<{ click: [] }>();
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

button.fio-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $margin-xs;
  width: 100%;
  border: none;
  border-radius: $border-radius-size;
  cursor: pointer;
  transition: background-color 0.2s ease;

  color: lighter(secondary, 90);

  font-size: $font-l-size;
  padding: $margin-s $margin;

  &--xs {
    width: auto;
    font-size: $font-s-size;
    padding: $margin-xxs $margin-xs;
  }

  &--s {
    width: auto;
    font-size: $font-size;
    padding: $margin-xs $margin-s;
  }

  &--m {
    width: auto;
    font-size: $font-l-size;
    padding: $margin-s $margin;
  }

  &--l {
    width: auto;
    font-size: $font-xl-size;
    padding: $margin $margin-l;
  }

  &--xl {
    width: auto;
    font-size: $font-xxl-size;
    padding: $margin-l $margin-xl;
  }

  &:disabled {
    opacity: 0.65;
    cursor: not-allowed;
  }

  &--submit {
    background-color: $primary;

    &:hover:not(:disabled) {
      background-color: darker(primary, 20);
    }

    &:active:not(:disabled) {
      background-color: darker(primary, 40);
    }
  }

  &--danger {
    background-color: $danger;

    &:hover:not(:disabled) {
      background-color: darker(danger, 20);
    }

    &:active:not(:disabled) {
      background-color: darker(danger, 40);
    }
  }

  &--ghost {
    background-color: lighter(secondary, 90);
    color: darker(secondary, 90);
    border: 1px solid lighter(secondary, 40);

    &:hover:not(:disabled) {
      background-color: lighter(primary, 70);
      color: darker(primary, 70);
    }

    &:active:not(:disabled) {
      background-color: lighter(primary, 60);
    }
  }
}

.fio-btn__icon {
  font-size: inherit;
}
</style>
