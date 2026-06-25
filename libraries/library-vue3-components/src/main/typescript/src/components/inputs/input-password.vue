<template>
  <div class="input-field">
    <label for="password" class="input-label">{{ label }}</label>
    <div class="input-wrapper">
      <input
        :type="!showPassword || disabled ? 'password' : 'text'"
        id="password"
        class="input-control"
        :class="{ 'input-control--invalid': !isValid && password.length > 0 }"
        :placeholder="label"
        v-model="password"
        :disabled="disabled"
        @input="emitInput($event)"
        @focus="emitFocus($event)"
        @blur="emitBlur($event)"
      />
      <button
        type="button"
        class="input-toggle"
        tabindex="-1"
        :disabled="disabled"
        @click="showPassword = !showPassword"
      >
        <fio-icon
          v-if="!showPassword || disabled"
          icon="eye"
          :disabled="disabled"
          :clickable="!disabled"
        />
        <fio-icon
          v-else
          icon="eye-slash"
          :disabled="disabled"
          :clickable="!disabled"
        />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import FioIcon from "../icon.vue";
import type { InputComponent } from "./model/input-component";

defineOptions({
  name: "FioInputPassword",
});

const {
  modelValue = "",
  label = "",
  isValid = true,
  disabled = false,
} = defineProps<
  InputComponent<string> & {
    label?: string;
    isValid?: boolean;
  }
>();

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

.input-wrapper {
  position: relative;
}

.input-control {
  padding-right: 2.75rem;
}

.input-toggle {
  position: absolute;
  right: $margin-xs;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  cursor: pointer;
  color: darker(secondary, 30);
  padding: $margin-xs;
  border-radius: $border-radius-size;
  display: flex;
  align-items: center;

  &:disabled {
    cursor: not-allowed;
    color: darker(secondary, 10);
  }
}
</style>
