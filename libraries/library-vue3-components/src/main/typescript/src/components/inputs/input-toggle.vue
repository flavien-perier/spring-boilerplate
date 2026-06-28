<template>
  <div
    class="input-field input-field--toggle"
    :class="[
      { 'input-field--disabled': disabled },
      size && `input-field--toggle--${size}`,
    ]"
  >
    <label :for="inputId" class="input-toggle">
      <input
        type="checkbox"
        :id="inputId"
        class="input-control input-control--toggle"
        :checked="modelValue"
        :disabled="disabled"
        @change="handleChange"
      />
      <span
        ref="trackRef"
        class="input-toggle__track"
        :class="{ 'input-toggle__track--dragging': dragging }"
        @pointerdown="onPointerDown"
      >
        <span
          ref="thumbRef"
          class="input-toggle__thumb"
          :style="thumbStyle"
        ></span>
      </span>
    </label>
    <span v-if="label" class="input-label input-label--toggle">{{
      label
    }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, useId } from "vue";
import type { InputComponent } from "./model/input-component";
import type { InputSize } from "@/model/input-size";
import { useDraggableToggle } from "./composables/use-draggable-toggle";

defineOptions({
  name: "FioInputToggle",
});

const inputId = useId();

const {
  modelValue = false,
  label,
  disabled = false,
  size,
} = defineProps<
  InputComponent<boolean> & { label?: string; size?: InputSize }
>();

const emit = defineEmits<{
  "update:modelValue": [value: boolean];
  change: [event: Event];
}>();

function handleChange(event: Event) {
  const target = event.target as HTMLInputElement;
  emit("update:modelValue", target.checked);
  emit("change", event);
}

const trackRef = ref<HTMLElement | null>(null);
const thumbRef = ref<HTMLElement | null>(null);

const { dragging, dragOffset, onPointerDown } = useDraggableToggle<boolean>({
  trackRef,
  thumbRef,
  stops: [false, true],
  isDisabled: () => disabled,
  onCommit: (value) => emit("update:modelValue", value),
});

const thumbStyle = computed(() =>
  dragOffset.value === null
    ? undefined
    : { transform: `translateX(${dragOffset.value}px)` }
);
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

.input-field--toggle {
  display: flex;
  align-items: center;
  gap: $margin-xs;

  --toggle-thumb: #{$font-l-size};
  --toggle-track-w: #{$font-xxl-size * 1.8};
  --toggle-track-h: calc(#{$font-l-size} + #{$margin-xxs * 2});

  &--xs {
    --toggle-thumb: #{$font-size};
    --toggle-track-w: #{$font-xl-size * 1.8};
    --toggle-track-h: calc(#{$font-size} + #{$margin-xxs * 2});
  }

  &--s {
    --toggle-thumb: #{$font-l-size * 0.85};
    --toggle-track-w: #{$font-xl-size * 1.7};
    --toggle-track-h: calc(#{$font-l-size * 0.85} + #{$margin-xxs * 2});
  }

  &--l {
    --toggle-thumb: #{$font-xl-size};
    --toggle-track-w: #{$font-xxl-size * 2.2};
    --toggle-track-h: calc(#{$font-xl-size} + #{$margin-xxs * 2});
  }

  &--xl {
    --toggle-thumb: #{$font-xxl-size};
    --toggle-track-w: #{$font-xxl-size * 2.6};
    --toggle-track-h: calc(#{$font-xxl-size} + #{$margin-xxs * 2});
  }
}

.input-label--toggle {
  margin-bottom: 0;
  cursor: pointer;
}

.input-control--toggle {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
  margin: 0;
}

.input-toggle {
  position: relative;
  display: inline-flex;
  cursor: pointer;
  flex-shrink: 0;
}

.input-toggle__track {
  display: inline-flex;
  align-items: center;
  width: var(--toggle-track-w);
  height: var(--toggle-track-h);
  padding: 0 $margin-xxs;
  background-color: lighter(secondary, 40);
  border: $border-size solid lighter(secondary, 40);
  border-radius: var(--toggle-track-h);
  box-sizing: border-box;
  transition: background-color 0.2s ease, border-color 0.2s ease;
  touch-action: none;
}

.input-toggle__track--dragging {
  background-color: $primary;
  border-color: $primary;
}

.input-toggle__thumb {
  display: block;
  width: var(--toggle-thumb);
  height: var(--toggle-thumb);
  background-color: lighter(secondary, 90);
  border-radius: 50%;
  transform: translateX(0);
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.input-toggle__track--dragging .input-toggle__thumb {
  transition: none;
}

.input-control--toggle:checked + .input-toggle__track {
  background-color: $primary;
  border-color: $primary;
}

.input-control--toggle:checked + .input-toggle__track .input-toggle__thumb {
  transform: translateX(
    calc(
      var(--toggle-track-w) - #{$border-size * 2} - #{$margin-xxs * 2} -
        var(--toggle-thumb)
    )
  );
  background-color: lighter(secondary, 90);
}

.input-control--toggle:focus-visible + .input-toggle__track {
  box-shadow: 0 0 0 $border-size lighter(primary, 20);
}

.input-field--disabled {
  .input-toggle {
    cursor: not-allowed;
  }

  .input-label--toggle {
    color: darker(secondary, 30);
    cursor: not-allowed;
  }

  .input-toggle__track {
    background-color: lighter(secondary, 70);
    border-color: lighter(secondary, 70);
  }

  .input-control--toggle:checked + .input-toggle__track {
    background-color: lighter(primary, 40);
    border-color: lighter(primary, 40);
  }
}
</style>
