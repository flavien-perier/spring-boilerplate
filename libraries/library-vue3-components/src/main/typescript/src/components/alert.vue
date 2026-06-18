<template>
  <div :class="['fio-alert', `fio-alert--${type}`]">
    {{ message }}
    <button class="fio-alert__close" @click="emit('close')">
      <fio-icon icon="xmark" />
    </button>
  </div>
</template>

<script setup lang="ts">
import FioIcon from "./icon.vue";

defineOptions({
  name: "FioAlert",
});

const props = defineProps<{
  type: "info" | "warning" | "danger";
  message: string;
}>();

const emit = defineEmits<{ close: [] }>();
</script>

<style scoped lang="scss">
@use "../styles/variables" as *;
@use "../styles/variables-colors" as *;

.fio-alert {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $margin-s $margin;
  border-radius: $border-radius-size;
  border: $border-size solid transparent;
  margin-bottom: $margin-s;

  &--info {
    background-color: lighter(info, 60);
    border-color: lighter(info, 40);
    color: darker(info, 60);
  }

  &--warning {
    background-color: lighter(warning, 60);
    border-color: lighter(warning, 40);
    color: darker(warning, 60);
  }

  &--danger {
    background-color: lighter(danger, 60);
    border-color: lighter(danger, 40);
    color: darker(danger, 60);
  }

  &__close {
    background: transparent;
    border: none;
    cursor: pointer;
    padding: 0;
    color: inherit;
    opacity: 0.7;
    display: flex;
    align-items: center;

    &:hover {
      opacity: 1;
    }
  }
}
</style>
