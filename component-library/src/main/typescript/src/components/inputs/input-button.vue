<template>
  <div v-if="variant === 'submit'" class="form-outline mb-1">
    <input
        type="button"
        class="btn btn-primary form-control form-control-lg"
        :value="label"
        :disabled="disabled"
        @click="emit('click')"
    />
  </div>
  <button v-else type="button" class="btn btn-danger mt-3 mb-4" @click="emit('click')">
    {{ label }}
  </button>
</template>

<script setup lang="ts">
defineOptions({
  name: "FioInputButton",
});

const { label, disabled, variant } = defineProps({
  label: { type: String, required: true },
  disabled: { type: Boolean, default: false },
  variant: { type: String, default: "submit" },
});

const emit = defineEmits(["click"]);
</script>
