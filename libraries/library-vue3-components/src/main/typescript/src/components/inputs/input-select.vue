<template>
  <div
    class="input-field"
    :class="{ 'input-field--disabled': disabled }"
    ref="rootRef"
  >
    <label
      v-if="label"
      :for="inputId"
      class="input-label"
      :class="size && `input-label--${size}`"
    >
      {{ label }}
    </label>
    <button
      :id="inputId"
      type="button"
      class="input-control input-control--select"
      :class="[
        size && `input-control--${size}`,
        { 'input-control--invalid': invalid },
      ]"
      :disabled="disabled"
      :aria-expanded="open"
      aria-haspopup="listbox"
      @click="toggle"
      @keydown="onTriggerKeydown"
    >
      <span v-if="selectedOption?.icon" class="input-select__trigger-icon">
        <fio-icon :icon="selectedOption.icon" size="s" />
      </span>
      <span class="input-select__trigger-label">
        {{ selectedOption?.label ?? placeholder ?? "" }}
      </span>
      <span
        class="input-select__chevron"
        :class="{ 'input-select__chevron--open': open }"
      >
        <fio-icon icon="chevron-down" size="s" />
      </span>
    </button>

    <ul
      v-if="open"
      class="input-select__dropdown"
      :class="size && `input-select__dropdown--${size}`"
      role="listbox"
      @keydown="onListKeydown"
    >
      <li
        v-for="(opt, i) in allOptions"
        :key="String(opt.value)"
        class="input-select__option"
        :class="{
          'input-select__option--active': i === activeIndex,
          'input-select__option--selected': opt.value === modelValue,
        }"
        role="option"
        :aria-selected="opt.value === modelValue"
        @click="select(opt)"
        @mousemove="activeIndex = i"
      >
        <span v-if="opt.icon" class="input-select__option-icon">
          <fio-icon :icon="opt.icon" size="s" />
        </span>
        <span>{{ opt.label }}</span>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, ref, useId, watch } from "vue";
import FioIcon from "../icon.vue";
import type { InputComponent } from "./model/input-component";
import type { InputSize } from "@/model/input-size";

defineOptions({
  name: "FioInputSelect",
});

export interface SelectOption {
  value: string | null;
  label: string;
  icon?: string;
}

const inputId = useId();

const props = withDefaults(
  defineProps<
    InputComponent<string | null> & {
      label?: string;
      nullOption?: string;
      placeholder?: string;
      options: SelectOption[];
      disabled?: boolean;
      invalid?: boolean;
      size?: InputSize;
    }
  >(),
  {
    modelValue: null,
    disabled: false,
    invalid: false,
  }
);

const emit = defineEmits<{
  "update:modelValue": [value: string | null];
  change: [value: string | null];
}>();

const rootRef = ref<HTMLElement | null>(null);
const open = ref(false);
const activeIndex = ref(0);

const allOptions = computed<SelectOption[]>(() => {
  const list: SelectOption[] = [];
  if (props.nullOption) {
    list.push({ value: null, label: props.nullOption });
  }
  list.push(...props.options);
  return list;
});

const selectedOption = computed(
  () => allOptions.value.find((o) => o.value === props.modelValue) ?? null
);

watch(open, (v) => {
  if (!v) return;
  const idx = allOptions.value.findIndex((o) => o.value === props.modelValue);
  activeIndex.value = idx >= 0 ? idx : 0;
});

function toggle() {
  if (props.disabled) return;
  open.value = !open.value;
}

function select(opt: SelectOption) {
  emit("update:modelValue", opt.value);
  emit("change", opt.value);
  open.value = false;
}

function onTriggerKeydown(event: KeyboardEvent) {
  if (event.key === "ArrowDown" || event.key === "Enter" || event.key === " ") {
    event.preventDefault();
    open.value = true;
  }
}

function onListKeydown(event: KeyboardEvent) {
  const count = allOptions.value.length;
  if (event.key === "ArrowDown") {
    event.preventDefault();
    activeIndex.value = (activeIndex.value + 1) % count;
  } else if (event.key === "ArrowUp") {
    event.preventDefault();
    activeIndex.value = (activeIndex.value - 1 + count) % count;
  } else if (event.key === "Enter") {
    event.preventDefault();
    select(allOptions.value[activeIndex.value]);
  } else if (event.key === "Escape") {
    event.preventDefault();
    open.value = false;
  } else if (event.key === "Tab") {
    open.value = false;
  } else if (event.key.length === 1) {
    const query = event.key.toLowerCase();
    const start = (activeIndex.value + 1) % count;
    for (let i = 0; i < count; i++) {
      const idx = (start + i) % count;
      if (allOptions.value[idx].label.toLowerCase().startsWith(query)) {
        activeIndex.value = idx;
        break;
      }
    }
  }
}

function onDocClick(event: MouseEvent) {
  if (!rootRef.value) return;
  if (!rootRef.value.contains(event.target as Node)) {
    open.value = false;
  }
}

watch(open, (v) => {
  if (v) {
    document.addEventListener("click", onDocClick);
  } else {
    document.removeEventListener("click", onDocClick);
  }
});

onBeforeUnmount(() => {
  document.removeEventListener("click", onDocClick);
});
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

.input-field {
  position: relative;
}

.input-control--select {
  display: flex;
  align-items: center;
  gap: $margin-xs;
  width: 100%;
  text-align: left;
  cursor: pointer;
  user-select: none;
}

.input-select__trigger-icon {
  flex-shrink: 0;
  color: darker(secondary, 50);
}

.input-select__trigger-label {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.input-select__chevron {
  flex-shrink: 0;
  color: darker(secondary, 40);
  transition: transform 0.2s ease, color 0.2s ease;
}

.input-select__chevron--open {
  transform: rotate(180deg);
  color: $primary;
}

.input-select__dropdown {
  position: absolute;
  z-index: 1000;
  list-style: none;
  margin: 0;
  padding: 0;
  width: 100%;
  background-color: lighter(secondary, 90);
  border: $border-size solid lighter(secondary, 40);
  border-radius: $border-radius-size;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  max-height: 12rem;

  &--xs {
    max-height: 10rem;
  }
  &--s {
    max-height: 10rem;
  }
}

.input-select__option {
  display: flex;
  align-items: center;
  gap: $margin-xs;
  padding: $margin-xs $margin;
  cursor: pointer;
  transition: background-color 0.1s ease;

  &--active {
    background-color: lighter(primary, 70);
    color: darker(primary, 70);
  }

  &--selected {
    font-weight: 600;
    color: $primary;
  }
}

.input-select__option-icon {
  flex-shrink: 0;
  color: darker(secondary, 50);
}

.input-field--disabled {
  .input-label {
    color: darker(secondary, 30);
    cursor: not-allowed;
  }

  .input-select__chevron,
  .input-select__trigger-icon {
    color: darker(secondary, 10);
  }
}
</style>
