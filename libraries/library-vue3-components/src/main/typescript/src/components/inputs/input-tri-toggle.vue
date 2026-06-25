<template>
  <div
    class="input-field input-field--tri-toggle"
    :class="{ 'input-field--disabled': disabled }"
  >
    <div class="input-tri-toggle">
      <input
        type="radio"
        :id="`${inputId}-false`"
        :name="inputId"
        class="input-control input-control--tri-toggle"
        value="false"
        :checked="modelValue === false"
        :disabled="disabled"
        @change="setValue(false)"
      />
      <input
        type="radio"
        :id="`${inputId}-null`"
        :name="inputId"
        class="input-control input-control--tri-toggle"
        value="null"
        :checked="modelValue === null"
        :disabled="disabled"
        @change="setValue(null)"
      />
      <input
        type="radio"
        :id="`${inputId}-true`"
        :name="inputId"
        class="input-control input-control--tri-toggle"
        value="true"
        :checked="modelValue === true"
        :disabled="disabled"
        @change="setValue(true)"
      />
      <span
        ref="trackRef"
        class="input-tri-toggle__track"
        :class="{ 'input-tri-toggle__track--dragging': dragging }"
        @pointerdown="onPointerDown"
      >
        <span
          ref="thumbRef"
          class="input-tri-toggle__thumb"
          :style="thumbStyle"
        ></span>
        <label
          :for="`${inputId}-false`"
          class="input-tri-toggle__zone input-tri-toggle__zone--false"
        ></label>
        <label
          :for="`${inputId}-null`"
          class="input-tri-toggle__zone input-tri-toggle__zone--null"
        ></label>
        <label
          :for="`${inputId}-true`"
          class="input-tri-toggle__zone input-tri-toggle__zone--true"
        ></label>
      </span>
    </div>
    <span v-if="label" class="input-label input-label--tri-toggle">{{
      label
    }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, useId } from "vue";
import type { InputComponent } from "./model/input-component";
import { useDraggableToggle } from "./composables/use-draggable-toggle";

defineOptions({
  name: "FioInputTriToggle",
});

const inputId = useId();

const {
  modelValue = null,
  label,
  disabled = false,
} = defineProps<InputComponent<boolean | null> & { label?: string }>();

const emit = defineEmits<{
  "update:modelValue": [value: boolean | null];
  change: [value: boolean | null];
}>();

function setValue(value: boolean | null) {
  emit("update:modelValue", value);
  emit("change", value);
}

const trackRef = ref<HTMLElement | null>(null);
const thumbRef = ref<HTMLElement | null>(null);

const { dragging, dragOffset, onPointerDown } = useDraggableToggle<
  boolean | null
>({
  trackRef,
  thumbRef,
  stops: [false, null, true],
  isDisabled: () => disabled,
  onCommit: (value) => setValue(value),
});

const thumbStyle = computed(() =>
  dragOffset.value === null
    ? undefined
    : { transform: `translate(${dragOffset.value}px, -50%)` }
);
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

$tri-track-width: $font-xxl-size * 2.5;
$tri-track-height: calc(#{$font-l-size} + #{$margin-xxs * 2});
$tri-thumb-size: $font-l-size;
$tri-step: calc(
  (
      #{$tri-track-width} - #{$border-size * 2} - #{$margin-xxs * 2} - #{$tri-thumb-size}
    ) / 2
);

.input-field--tri-toggle {
  display: flex;
  align-items: center;
  gap: $margin-xs;
}

.input-label--tri-toggle {
  margin-bottom: 0;
  cursor: pointer;
}

.input-control--tri-toggle {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
  margin: 0;
}

.input-tri-toggle {
  position: relative;
  display: inline-flex;
  cursor: pointer;
  flex-shrink: 0;
}

.input-tri-toggle__track {
  position: relative;
  display: inline-flex;
  align-items: center;
  width: $tri-track-width;
  height: $tri-track-height;
  padding: 0 $margin-xxs;
  background-color: lighter(secondary, 40);
  border: $border-size solid lighter(secondary, 40);
  border-radius: $tri-track-height;
  box-sizing: border-box;
  transition: background-color 0.2s ease, border-color 0.2s ease;
  touch-action: none;
}

.input-tri-toggle__track--dragging {
  transition: none;
}

.input-tri-toggle__track--dragging .input-tri-toggle__thumb {
  transition: none;
}

.input-tri-toggle__thumb {
  position: absolute;
  top: 50%;
  left: $margin-xxs;
  display: block;
  width: $tri-thumb-size;
  height: $tri-thumb-size;
  background-color: lighter(secondary, 90);
  border-radius: 50%;
  transform: translate(0, -50%);
  transition: transform 0.2s ease, background-color 0.2s ease;
  pointer-events: none;
}

.input-tri-toggle__zone {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 33.333%;
  cursor: pointer;
}

.input-tri-toggle__zone--false {
  left: 0;
}

.input-tri-toggle__zone--null {
  left: 33.333%;
}

.input-tri-toggle__zone--true {
  right: 0;
}

.input-control--tri-toggle:nth-child(1):checked ~ .input-tri-toggle__track {
  background-color: $danger;
  border-color: $danger;
}

.input-control--tri-toggle:nth-child(2):checked ~ .input-tri-toggle__track {
  background-color: lighter(secondary, 40);
  border-color: lighter(secondary, 40);
}

.input-control--tri-toggle:nth-child(3):checked ~ .input-tri-toggle__track {
  background-color: $success;
  border-color: $success;
}

.input-control--tri-toggle:nth-child(1):checked
  ~ .input-tri-toggle__track
  .input-tri-toggle__thumb {
  transform: translate(0, -50%);
}

.input-control--tri-toggle:nth-child(2):checked
  ~ .input-tri-toggle__track
  .input-tri-toggle__thumb {
  transform: translate($tri-step, -50%);
}

.input-control--tri-toggle:nth-child(3):checked
  ~ .input-tri-toggle__track
  .input-tri-toggle__thumb {
  transform: translate(calc(#{$tri-step} * 2), -50%);
}

.input-control--tri-toggle:focus-visible ~ .input-tri-toggle__track {
  box-shadow: 0 0 0 $border-size lighter(primary, 20);
}

.input-field--disabled {
  .input-tri-toggle {
    cursor: not-allowed;
  }

  .input-label--tri-toggle {
    color: darker(secondary, 30);
    cursor: not-allowed;
  }

  .input-tri-toggle__zone {
    cursor: not-allowed;
  }

  .input-tri-toggle__track {
    background-color: lighter(secondary, 70);
    border-color: lighter(secondary, 70);
  }

  .input-control--tri-toggle:nth-child(1):checked ~ .input-tri-toggle__track {
    background-color: lighter(danger, 40);
    border-color: lighter(danger, 40);
  }

  .input-control--tri-toggle:nth-child(2):checked ~ .input-tri-toggle__track {
    background-color: lighter(secondary, 70);
    border-color: lighter(secondary, 70);
  }

  .input-control--tri-toggle:nth-child(3):checked ~ .input-tri-toggle__track {
    background-color: lighter(success, 40);
    border-color: lighter(success, 40);
  }
}
</style>
