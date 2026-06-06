<template>
  <div class="input-field">
    <label v-if="label" :for="inputId" class="input-label">{{ label }}</label>
    <input
      type="text"
      :id="inputId"
      class="input-control"
      :class="{ 'input-control--invalid': isInvalid }"
      :placeholder="placeholder"
      :maxlength="maxLength"
      :value="value"
      :disabled="disabled"
      @input="handleInput"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, useId, watch } from "vue";

defineOptions({
  name: "FioInputText",
});

const inputId = useId();

const props = defineProps<{
  placeholder?: string;
  pattern?: RegExp;
  label?: string;
  maxLength?: number;
  allowedCharacters?: RegExp;
  disabled?: boolean;
}>();

const emit = defineEmits<{
  input: [event: Event];
  "update:isValid": [value: boolean];
}>();

const value = defineModel<string>({ default: "" });

const safePattern = computed(() => {
  if (!props.pattern) return null;
  const flags = props.pattern.flags.replace(/[gy]/g, "");
  return new RegExp(props.pattern.source, flags);
});

const safeAllowedCharacters = computed(() => {
  if (!props.allowedCharacters) return null;
  const flags = props.allowedCharacters.flags.replace(/[gy]/g, "");
  return new RegExp(props.allowedCharacters.source, flags);
});

function testPattern(pattern: RegExp, value: string): boolean {
  pattern.lastIndex = 0;
  return pattern.test(value);
}

const isInvalid = computed(
  () =>
    value.value.length > 0 &&
    !!safePattern.value &&
    !testPattern(safePattern.value, value.value)
);

watch(
  value,
  (newValue) => {
    if (!safePattern.value) return;
    emit(
      "update:isValid",
      newValue.length === 0 || testPattern(safePattern.value, newValue)
    );
  },
  { immediate: true }
);

function handleInput(event: Event) {
  const target = event.target as HTMLInputElement;
  let newValue = target.value;

  if (safeAllowedCharacters.value) {
    const allowed = safeAllowedCharacters.value;
    newValue = newValue
      .split("")
      .filter((c) => testPattern(allowed, c))
      .join("");
  }

  if (props.maxLength !== undefined && newValue.length > props.maxLength) {
    newValue = newValue.slice(0, props.maxLength);
  }

  if (!safePattern.value || testPattern(safePattern.value, newValue)) {
    value.value = newValue;
    if (target.value !== newValue) {
      target.value = newValue;
    }
    emit(
      "update:isValid",
      !safePattern.value || testPattern(safePattern.value, value.value)
    );
    emit("input", event);
  } else {
    const pos = target.selectionStart;
    target.value = value.value;
    if (pos !== null) target.setSelectionRange(pos, pos);
  }
}
</script>
