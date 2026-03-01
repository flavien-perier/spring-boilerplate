<template>
  <div class="form-outline mb-4">
    <label for="email" class="form-label">{{ $t("email")  }}</label>
    <input
        type="text"
        id="email"
        class="form-control form-control-lg z-0"
        :class="{'is-invalid': !isValid && email.length > 0}"
        :placeholder="$t('email')"
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
  name: "InputEmail",
});

const isValid = ref(true);

const { modelValue } = defineProps({
  modelValue: { type: String, default: "" },
});

const emit = defineEmits(["update:modelValue", "update:isValid", "input", "focus", "blur"]);

const email = computed({
  get: () => modelValue,
  set: value => {
    isValid.value = checkEmail(value);
    emit("update:isValid", isValid.value);
    emit("update:modelValue", value);
  }
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

<style scoped>
.password-requirements {
  font-size: 0.9rem;
}
</style>