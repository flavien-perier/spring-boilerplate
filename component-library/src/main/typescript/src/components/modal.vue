<template>
  <div v-if="show" class="fio-modal">
    <div class="fio-modal__dialog">
      <div class="fio-modal__header">
        <h5 class="fio-modal__title">{{ title }}</h5>
        <button class="fio-modal__close" @click="emit('close')">
          <fio-icon icon="xmark" />
        </button>
      </div>
      <div class="fio-modal__body">
        <p>{{ content }}</p>
      </div>
      <div class="fio-modal__footer" v-if="rejectMessage || resolveMessage">
        <button
          class="fio-modal__btn fio-modal__btn--secondary"
          v-if="rejectMessage"
          @click="emit('close')"
        >
          {{ rejectMessage }}
        </button>
        <button
          class="fio-modal__btn fio-modal__btn--primary"
          v-if="resolveMessage"
          @click="emit('resolve')"
        >
          {{ resolveMessage }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import FioIcon from "./fio-icon.vue";

defineOptions({
  name: "FioModal",
});

withDefaults(
  defineProps<{
    show: boolean;
    title: string;
    content: string;
    rejectMessage?: string;
    resolveMessage?: string;
  }>(),
  {
    rejectMessage: "",
    resolveMessage: "",
  }
);

const emit = defineEmits<{ close: []; resolve: [] }>();
</script>

<style scoped lang="scss">
@use "../styles/variables" as *;
@use "../styles/colors" as *;

.fio-modal {
  position: fixed;
  inset: 0;
  background: rgba($secondary-darker-90, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;

  &__dialog {
    background: $secondary-lighter-90;
    border-radius: 8px;
    max-width: 500px;
    width: calc(100% - 2rem);
    margin: 1rem;
    box-shadow: 0 4px 24px rgba($secondary-darker-90, 0.2);
  }

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $margin $margin;
    border-bottom: 1px solid $secondary-lighter-80;
  }

  &__title {
    margin: 0;
    font-size: $font-l-size;
    font-weight: 600;
  }

  &__body {
    padding: $margin;
  }

  &__footer {
    display: flex;
    justify-content: flex-end;
    gap: $margin-s;
    padding: $margin;
    border-top: 1px solid $secondary-lighter-80;
  }

  &__close {
    background: transparent;
    border: none;
    cursor: pointer;
    opacity: 0.7;
    display: flex;
    align-items: center;
    padding: 0;

    &:hover {
      opacity: 1;
    }
  }

  &__btn {
    padding: $margin-xs $margin-s;
    border-radius: $border-radius-size;
    border: 1px solid transparent;
    cursor: pointer;
    font-size: $font-size;
    transition: opacity 0.15s;

    &:hover {
      opacity: 0.85;
    }

    &--primary {
      background-color: $primary;
      color: $secondary-lighter-90;
    }

    &--secondary {
      background-color: $secondary;
      color: $secondary-lighter-90;
    }
  }
}
</style>
