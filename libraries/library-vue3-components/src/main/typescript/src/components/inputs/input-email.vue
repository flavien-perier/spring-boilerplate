<template>
  <fio-input-text
    :model-value="modelValue"
    :label="$t('fio.email')"
    :placeholder="$t('fio.email')"
    :invalid="!isValid && modelValue.length > 0"
    :disabled="disabled"
    :size="size"
    @update:model-value="(v) => emit('update:modelValue', v)"
    @input="(e) => emit('input', e)"
    @focus="(e) => emit('focus', e)"
    @blur="(e) => emit('blur', e)"
  />
</template>

<script setup lang="ts">
import { computed, watch } from "vue";
import { useI18n } from "vue-i18n";
import FioInputText from "./input-text.vue";
import type { InputComponent } from "./model/input-component";
import type { InputSize } from "@/model/input-size";

defineOptions({
  name: "FioInputEmail",
});

const { t: $t } = useI18n();

const {
  modelValue = "",
  disabled = false,
  size,
} = defineProps<InputComponent<string> & { size?: InputSize }>();

const emit = defineEmits([
  "update:modelValue",
  "update:isValid",
  "input",
  "focus",
  "blur",
]);

// Intentionally flagless: a `g` flag would make `.test()` stateful.
const emailPattern = /^((?!\.)[\w\-_.]*[^.])(@\w+)(\.\w+(\.\w+)?[^.\W])$/;

const isValid = computed(
  () => modelValue.length > 0 && emailPattern.test(modelValue)
);

watch(isValid, (value) => emit("update:isValid", value), { immediate: true });
</script>
