<template>
  <div class="input-field">
    <label for="email" class="input-label">{{ $t("fio.email") }}</label>
    <input
      type="text"
      id="email"
      class="input-control"
      :class="{ 'input-control--invalid': !isValid && email.length > 0 }"
      :placeholder="$t('fio.email')"
      v-model="email"
      @input="emitInput($event)"
      @focus="emitFocus($event)"
      @blur="emitBlur($event)"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";

defineOptions({
  name: "FioInputEmail",
});

const isValid = ref(true);

const { modelValue } = defineProps({
  modelValue: { type: String, default: "" },
});

const emit = defineEmits([
  "update:modelValue",
  "update:isValid",
  "input",
  "focus",
  "blur",
]);

const email = computed({
  get: () => modelValue,
  set: (value) => {
    isValid.value = checkEmail(value);
    emit("update:isValid", isValid.value);
    emit("update:modelValue", value);
  },
});

function checkEmail(email: string) {
  return /^((?!\.)[\w\-_.]*[^.])(@\w+)(\.\w+(\.\w+)?[^.\W])$/gm.test(email);
}

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

.input-control {
  width: 100%;
  padding: $margin-s $margin;
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
</style>
