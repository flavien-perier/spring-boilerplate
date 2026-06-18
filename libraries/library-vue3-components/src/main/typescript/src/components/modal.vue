<template>
  <div v-if="show" class="fio-modal">
    <div class="fio-modal__dialog">
      <div class="fio-modal__header">
        <h5 class="fio-modal__title">{{ title }}</h5>
        <fio-icon icon="xmark" clickable class="fio-modal__close" @click="emit('close')"/>
      </div>
      <div class="fio-modal__body">
        <p>{{ content }}</p>
      </div>
      <div class="fio-modal__footer" v-if="rejectMessage || resolveMessage">
        <fio-input-button
          v-if="rejectMessage"
          :label="rejectMessage"
          variant="danger"
          size="s"
          @click="emit('close')"
        />
        <fio-input-button
          v-if="resolveMessage"
          :label="resolveMessage"
          size="s"
          @click="emit('resolve')"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import FioIcon from "./icon.vue";
import FioInputButton from "./inputs/input-button.vue";

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
@use "../styles/variables-colors" as *;

.fio-modal {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;

  &__dialog {
    background: lighter(secondary, 90);
    border-radius: $border-radius-size;
    border: $border-size solid lighter(secondary, 70);
    max-width: 32rem;
    width: calc(100% - 2rem);
    margin: $margin;
  }

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $margin $margin;
    border-bottom: $border-size solid lighter(secondary, 70);
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
    border-top: $border-size solid lighter(secondary, 70);
  }

  &__close {
    opacity: 0.7;

    &:hover {
      opacity: 1;
    }
  }
}
</style>
