<template>
  <div class="input-field" :class="{ 'input-field--disabled': disabled }">
    <label v-if="label" :for="inputId" class="input-label">{{ label }}</label>
    <select
      :id="inputId"
      class="input-control input-control--select"
      :value="modelValue ?? ''"
      :disabled="disabled"
      @change="handleChange"
    >
      <option v-if="nullOption" :value="null" :selected="modelValue === null">
        {{ nullOption }}
      </option>
      <option
        v-for="option in options"
        :key="option.value"
        :value="option.value"
        :selected="modelValue === option.value"
      >
        {{ option.label }}
      </option>
    </select>
  </div>
</template>

<script setup lang="ts">
import { useId } from "vue";

defineOptions({
  name: "FioInputSelect",
});

export interface SelectOption {
  value: string;
  label: string;
}

const inputId = useId();

const {
  label,
  nullOption,
  options,
  disabled = false,
} = defineProps<{
  label?: string;
  nullOption?: string;
  options: SelectOption[];
  disabled?: boolean;
}>();

const modelValue = defineModel<string | null>({ default: null });

const emit = defineEmits<{ change: [event: Event] }>();

function handleChange(event: Event) {
  const target = event.target as HTMLSelectElement;
  const selected = options.find((option) => option.value === target.value);
  modelValue.value = selected ? selected.value : null;
  emit("change", event);
}
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

.input-control--select {
  width: 100%;
  padding: $margin-s $margin;
  font-size: $font-l-size;
  background-color: lighter(secondary, 90);
  color: darker(secondary, 90);
  border: $border-size solid lighter(secondary, 40);
  border-radius: $border-radius-size;
  outline: none;
  box-sizing: border-box;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;

  &:focus {
    border-color: $primary;
    box-shadow: 0 0 0 $border-size lighter(primary, 20);
  }

  &:disabled {
    background-color: lighter(secondary, 70);
    color: darker(secondary, 50);
    cursor: not-allowed;
  }
}

.input-field--disabled {
  .input-label {
    color: darker(secondary, 30);
    cursor: not-allowed;
  }
}
</style>
