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
      :disabled="disabled"
      @input="emitInput($event)"
      @focus="emitFocus($event)"
      @blur="emitBlur($event)"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import type { InputComponent } from "./model/input-component";

defineOptions({
  name: "FioInputEmail",
});

const isValid = ref(true);

const { modelValue = "", disabled = false } =
  defineProps<InputComponent<string>>();

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

onMounted(() => {
  isValid.value = checkEmail(modelValue);
  emit("update:isValid", isValid.value);
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
