<template>
  <label for="password" class="form-label">{{ label }}</label>
  <div class="input-group">
    <input
        :type="!showPassword ? 'password' : 'text'"
        id="password"
        class="form-control form-control-lg z-0"
        :class="{'is-invalid': !isValid && password.length > 0}"
        :placeholder="label"
        v-model="password"
        @input="emitInput($event)"
        @focus="emitFocus($event)"
        @blur="emitBlur($event)"
    />
    <span class="input-group-text cursor-pointer" @click="showPassword = !showPassword" >
        <font-awesome-icon v-if="!showPassword" icon="eye" />
        <font-awesome-icon v-else icon="eye-slash" />
      </span>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";

defineOptions({
  name: "InputPassword",
});

const { modelValue } = defineProps({
  modelValue: { type: String, default: "" },
  label: { type: String, default: "" },
  isValid: { type: Boolean, default: true },
});

const emit = defineEmits(["update:modelValue", "input", "focus", "blur"]);

const password = computed({
  get: () => modelValue,
  set: (value) => emit("update:modelValue", value)
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

<style scoped>
.password-requirements {
  font-size: 0.9rem;
}
</style>