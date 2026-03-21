<template>
  <div :class="['fio-alert', `fio-alert--${type}`]">
    {{ message }}
    <button class="fio-alert__close" @click="emit('close')">
      <fio-icon icon="xmark" />
    </button>
  </div>
</template>

<script setup lang="ts">
import FioIcon from "./fio-icon.vue";

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
@use "../styles/colors" as *;

.fio-alert {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $margin-s $margin;
  border-radius: $border-radius-size;
  border: 1px solid transparent;
  margin-bottom: $margin-s;

  &--info {
    background-color: $info-lighter-60;
    border-color: $info-lighter-40;
    color: $info-darker-60;
  }

  &--warning {
    background-color: $warning-lighter-60;
    border-color: $warning-lighter-40;
    color: $warning-darker-60;
  }

  &--danger {
    background-color: $danger-lighter-60;
    border-color: $danger-lighter-40;
    color: $danger-darker-60;
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
