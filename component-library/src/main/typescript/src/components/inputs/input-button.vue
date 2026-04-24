<template>
  <button
    type="button"
    class="fio-btn"
    :class="variant"
    :disabled="disabled || waiting"
    @click="emit('click')"
  >
    <fio-icon :icon="waiting ? 'spinner' : icon" class="fio-btn__icon" v-if="waiting || icon" />
    {{ label }}
  </button>
</template>

<script setup lang="ts">
import FioIcon from "../fio-icon.vue";

defineOptions({
  name: "FioInputButton",
});

const {
  label,
  disabled = false,
  waiting = false,
  variant = "submit",
  icon,
} = defineProps<{
  label: string;
  disabled?: boolean;
  waiting?: boolean;
  variant?: "submit" | "warning" | "danger";
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

  color: $secondary-lighter-90;

  font-size: $font-l-size;
  padding: $margin-s $margin;

  &:disabled {
    opacity: 0.65;
    cursor: not-allowed;
  }

  &.submit {
    background-color: $primary;

    &:hover:not(:disabled) {
      background-color: $primary-darker-20;
    }
  }

  &.danger {
    background-color: $danger;

    &:hover:not(:disabled) {
      background-color: $danger-darker-20;
    }
  }
}

.fio-btn__icon {
  font-size: inherit;
}
</style>
