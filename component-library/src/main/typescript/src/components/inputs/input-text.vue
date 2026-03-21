<template>
  <input
    type="text"
    class="fio-input"
    :placeholder="placeholder"
    v-model="value"
    @input="emit('input', $event)"
  />
</template>

<script setup lang="ts">
import { computed } from "vue";

defineOptions({
  name: "FioInputText",
});

const { modelValue, placeholder } = withDefaults(
  defineProps<{
    modelValue?: string;
    placeholder?: string;
  }>(),
  {
    modelValue: "",
    placeholder: "",
  }
);

const emit = defineEmits<{
  "update:modelValue": [value: string];
  input: [event: Event];
}>();

const value = computed({
  get: () => modelValue,
  set: (v) => emit("update:modelValue", v),
});
</script>

<style scoped lang="scss">
@use "../../styles/variables" as *;
@use "../../styles/colors" as *;

.fio-input {
  display: block;
  width: 100%;
  padding: $margin-xs $margin-s;
  font-size: $font-size;
  border: 1px solid $secondary-lighter-70;
  border-radius: $border-radius-size;
  background-color: $secondary-lighter-90;
  transition: border-color 0.15s, box-shadow 0.15s;

  &:focus {
    outline: none;
    border-color: $primary;
    box-shadow: 0 0 0 3px rgba($primary, 0.25);
  }

  &--invalid {
    border-color: $danger;

    &:focus {
      box-shadow: 0 0 0 3px rgba($danger, 0.25);
    }
  }
}
</style>
