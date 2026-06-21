<template>
  <div class="input-field input-field--toggle" :class="{ 'input-field--disabled': disabled }">
    <label :for="inputId" class="input-toggle">
      <input
          type="checkbox"
          :id="inputId"
          class="input-control input-control--toggle"
          :checked="modelValue"
          :disabled="disabled"
          @change="handleChange"
      />
      <span class="input-toggle__track">
        <span class="input-toggle__thumb"></span>
      </span>
    </label>
    <span v-if="label" class="input-label input-label--toggle">{{ label }}</span>
  </div>
</template>

<script setup lang="ts">
import {useId} from "vue";

defineOptions({
  name: "FioInputToggle",
});

const inputId = useId();

const {label, disabled = false} = defineProps<{
  label?: string;
  disabled?: boolean;
}>();

const modelValue = defineModel<boolean>({default: false});

const emit = defineEmits<{ change: [event: Event] }>();

function handleChange(event: Event) {
  const target = event.target as HTMLInputElement;
  modelValue.value = target.checked;
  emit("change", event);
}
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

.input-field--toggle {
  display: flex;
  align-items: center;
  gap: $margin-xs;
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
  width: $font-xxl-size * 1.8;
  height: calc(#{$font-l-size} + #{$margin-xxs * 2});
  padding: 0 $margin-xxs;
  background-color: lighter(secondary, 40);
  border: $border-size solid lighter(secondary, 40);
  border-radius: calc(#{$font-l-size} + #{$margin-xxs * 2});
  box-sizing: border-box;
  transition: background-color 0.2s ease, border-color 0.2s ease;
}

.input-toggle__thumb {
  display: block;
  width: $font-l-size;
  height: $font-l-size;
  background-color: lighter(secondary, 90);
  border-radius: 50%;
  transform: translateX(0);
  transition: transform 0.2s ease, background-color 0.2s ease;
}

.input-control--toggle:checked + .input-toggle__track {
  background-color: $primary;
  border-color: $primary;
}

.input-control--toggle:checked + .input-toggle__track .input-toggle__thumb {
  transform: translateX(
      calc(
          #{$font-xxl-size * 1.8} - #{$border-size * 2} - #{$margin-xxs * 2} - #{$font-l-size}
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
