<template>
  <button
    type="button"
    class="fio-btn"
    :class="variant"
    :disabled="disabled"
    @click="emit('click')"
  >
    {{ label }}
  </button>
</template>

<script setup lang="ts">
defineOptions({
  name: "FioInputButton",
});

const {
  label,
  disabled = false,
  variant = "submit",
} = defineProps<{
  label: string;
  disabled?: boolean;
  variant?: "submit" | "warning" | "danger";
}>();

const emit = defineEmits<{ click: [] }>();
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

button.fio-btn {
  display: block;
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
</style>
