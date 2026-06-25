<template>
  <div
    class="input-field input-field--checkbox"
    :class="{ 'input-field--disabled': disabled }"
  >
    <input
      type="checkbox"
      :id="inputId"
      class="input-control input-control--checkbox"
      :checked="modelValue"
      :disabled="disabled"
      @change="handleChange"
    />
    <label
      v-if="label"
      :for="inputId"
      class="input-label input-label--checkbox"
      >{{ label }}</label
    >
  </div>
</template>

<script setup lang="ts">
import { useId } from "vue";
import type { InputComponent } from "./model/input-component";

defineOptions({
  name: "FioInputCheckbox",
});

const inputId = useId();

const {
  modelValue = false,
  label,
  disabled = false,
} = defineProps<InputComponent<boolean> & { label?: string }>();

const emit = defineEmits<{
  "update:modelValue": [value: boolean];
  change: [event: Event];
}>();

function handleChange(event: Event) {
  const target = event.target as HTMLInputElement;
  emit("update:modelValue", target.checked);
  emit("change", event);
}
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

.input-field--checkbox {
  display: flex;
  align-items: center;
  gap: $margin-xs;
}

.input-label--checkbox {
  margin-bottom: 0;
  cursor: pointer;
}

.input-control--checkbox {
  width: $font-l-size;
  height: $font-l-size;
  flex-shrink: 0;
  margin: 0;
  padding: 0;
  accent-color: $primary;
  cursor: pointer;

  &:disabled {
    cursor: not-allowed;
  }
}

.input-field--disabled {
  .input-label--checkbox {
    color: darker(secondary, 30);
    cursor: not-allowed;
  }
}
</style>
