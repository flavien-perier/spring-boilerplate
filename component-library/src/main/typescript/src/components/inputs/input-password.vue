<template>
  <div class="input-field">
    <label for="password" class="input-label">{{ label }}</label>
    <div class="input-wrapper">
      <input
        :type="!showPassword ? 'password' : 'text'"
        id="password"
        class="input-control"
        :class="{ 'input-control--invalid': !isValid && password.length > 0 }"
        :placeholder="label"
        v-model="password"
        @input="emitInput($event)"
        @focus="emitFocus($event)"
        @blur="emitBlur($event)"
      />
      <button
        type="button"
        class="input-toggle"
        tabindex="-1"
        @click="showPassword = !showPassword"
      >
        <fio-icon v-if="!showPassword" icon="eye" />
        <fio-icon v-else icon="eye-slash" />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import FioIcon from "../fio-icon.vue";

defineOptions({
  name: "FioInputPassword",
});

const {
  modelValue = "",
  label = "",
  isValid = true,
} = defineProps<{
  modelValue?: string;
  label?: string;
  isValid?: boolean;
}>();

const emit = defineEmits<{
  "update:modelValue": [value: string];
  input: [event: Event];
  focus: [event: Event];
  blur: [event: Event];
}>();

const password = computed({
  get: () => modelValue,
  set: (value) => emit("update:modelValue", value),
});

const showPassword = ref(false);

function emitInput(event: Event) {
  emit("input", event);
}

function emitFocus(event: Event) {
  emit("focus", event);
}

function emitBlur(event: Event) {
  emit("blur", event);
}
</script>

<style scoped lang="scss">
@use "../../styles/variables" as *;
@use "../../styles/variables-colors" as *;

.input-label {
  display: block;
  margin-bottom: $margin-xxs;
  color: $secondary-darker-60;
  font-size: $font-size;
}

.input-wrapper {
  position: relative;
}

.input-control {
  width: 100%;
  padding: $margin-s $margin;
  padding-right: 2.75rem;
  font-size: $font-l-size;
  border: 1px solid $secondary-lighter-20;
  border-radius: $border-radius-size;
  outline: none;
  box-sizing: border-box;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;

  &:focus {
    border-color: $primary;
    box-shadow: 0 0 0 3px $primary-lighter-20;
  }

  &--invalid {
    border-color: $danger;

    &:focus {
      box-shadow: 0 0 0 3px $danger-lighter-20;
    }
  }
}

.input-toggle {
  position: absolute;
  right: $margin-xs;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  cursor: pointer;
  color: $secondary;
  padding: $margin-xs;
  border-radius: $border-radius-size;
  display: flex;
  align-items: center;
  transition: color 0.2s ease;

  &:hover {
    color: $secondary-darker-40;
  }
}
</style>
