<template>
  <div
    class="permission-setting"
    :class="{ 'permission-setting--disabled': disabled || locked }"
  >
    <span class="permission-setting__name">{{ permission }}</span>
    <div class="permission-setting__control permission-setting__control--override">
      <fio-input-checkbox
        v-model="override"
        :label="overrideLabel"
        :disabled="disabled || locked"
        @change="handleOverrideChange"
      />
    </div>
    <div class="permission-setting__control permission-setting__control--allow">
      <fio-input-toggle
        v-model="allow"
        :label="allow ? allowLabel : denyLabel"
        :disabled="disabled || locked || !override"
        @change="handleAllowChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import FioInputCheckbox from "./inputs/input-checkbox.vue";
import FioInputToggle from "./inputs/input-toggle.vue";

defineOptions({
  name: "FioPermissionSetting",
});

const {
  permission,
  overrideLabel,
  allowLabel,
  denyLabel,
  disabled = false,
  locked = false,
} = defineProps<{
  permission: string;
  overrideLabel?: string;
  allowLabel?: string;
  denyLabel?: string;
  disabled?: boolean;
  locked?: boolean;
}>();

const override = defineModel<boolean>("override", { default: false });
const allow = defineModel<boolean>("allow", { default: false });

const emit = defineEmits<{
  change: [state: { permission: string; override: boolean; allow: boolean }];
}>();

function handleOverrideChange(event: Event) {
  emit("change", {
    permission,
    override: override.value,
    allow: allow.value,
  });
  void event;
}

function handleAllowChange(event: Event) {
  emit("change", {
    permission,
    override: override.value,
    allow: allow.value,
  });
  void event;
}
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

.permission-setting {
  display: flex;
  align-items: center;
  gap: $margin;
}

.permission-setting__name {
  flex: 1 1 auto;
  min-width: 0;
  font-size: $font-size;
  color: darker(secondary, 90);
}

/* Fixed-basis columns keep the controls aligned across rows even when the
   toggle label switches between allow / deny (different text widths). */
.permission-setting__control {
  display: flex;
  align-items: center;
  flex: 0 0 auto;
}

.permission-setting__control--override {
  flex-basis: 10rem;
}

.permission-setting__control--allow {
  flex-basis: 9rem;
}

.permission-setting--disabled {
  .permission-setting__name {
    color: darker(secondary, 30);
    cursor: not-allowed;
  }
}
</style>
