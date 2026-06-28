<template>
  <div class="fio-create-password">
    <div class="password-field">
      <input-password
        :label="$t('fio.password')"
        v-model="password"
        :is-valid="constraintsValid"
        :disabled="disabled"
        @focus="showPasswordInfo = true"
        @blur="showPasswordInfo = false"
      />

      <Transition name="constraints">
        <ul
          class="constraints-list"
          v-if="showPasswordInfo && !constraintsValid"
        >
          <li
            v-for="constraint in passwordConstraints"
            :key="constraint.i18nKey"
          >
            <fio-icon
              :icon="constraint.valid ? 'circle-check' : 'circle-xmark'"
              :color="constraint.valid ? 'success' : 'danger'"
            />
            {{ $t(constraint.i18nKey, constraint.params) }}
          </li>
        </ul>
      </Transition>
    </div>

    <input-password
      :label="$t('fio.repeat-password')"
      v-model="confirmPassword"
      :is-valid="passwordMatch"
      :disabled="disabled"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { useI18n } from "vue-i18n";
import { passwordUtil } from "../../utils/password-util";
import InputPassword from "./input-password.vue";
import FioIcon from "../icon.vue";
import type { InputComponent } from "./model/input-component";

defineOptions({
  name: "FioInputCreatePassword",
});

const props = withDefaults(
  defineProps<
    InputComponent<string> & {
      minPasswordLength?: number;
    }
  >(),
  { modelValue: "", minPasswordLength: 8, disabled: false }
);

const emit = defineEmits(["update:modelValue", "update:isValid"]);

const { t: $t } = useI18n();

const password = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value),
});

const confirmPassword = ref("");

const showPasswordInfo = ref(false);

interface PasswordConstraint {
  i18nKey: string;
  params?: Record<string, unknown>;
  valid: boolean;
}

const passwordConstraints = computed<PasswordConstraint[]>(() => [
  {
    i18nKey: "fio.password.constraint.length",
    params: { minPasswordLength: props.minPasswordLength },
    valid: passwordUtil.checkPasswordLength(
      password.value,
      props.minPasswordLength
    ),
  },
  {
    i18nKey: "fio.password.constraint.lowercase",
    valid: passwordUtil.checkPasswordLowercase(password.value),
  },
  {
    i18nKey: "fio.password.constraint.uppercase",
    valid: passwordUtil.checkPasswordUppercase(password.value),
  },
  {
    i18nKey: "fio.password.constraint.number",
    valid: passwordUtil.checkPasswordNumber(password.value),
  },
  {
    i18nKey: "fio.password.constraint.special-character",
    valid: passwordUtil.checkSpecialCharacter(password.value),
  },
]);

const constraintsValid = computed(() =>
  passwordConstraints.value.every((c) => c.valid)
);

const passwordMatch = computed(
  () => password.value === confirmPassword.value && confirmPassword.value !== ""
);

const isPasswordValid = computed(
  () => constraintsValid.value && passwordMatch.value
);

watch(isPasswordValid, (value) => emit("update:isValid", value), {
  immediate: true,
});
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/variables-colors" as *;

.password-field {
  position: relative;
  margin-bottom: $margin-xl;
}

.constraints-list {
  position: absolute;
  top: 100%;
  list-style: none;
  margin: 0;
  padding: $margin-s $margin;
  display: flex;
  flex-direction: column;
  gap: $margin-xs;
  background-color: lighter(secondary, 90);
  border: $border-size solid lighter(secondary, 80);
  border-radius: $border-radius-size;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 3;
  margin-top: $margin-xxs;
  min-width: 16rem;

  li {
    display: flex;
    align-items: center;
    gap: $margin-xs;
    font-size: $font-s-size;
    color: $secondary;
    transition: color 0.2s ease;
  }
}

.constraints-enter-active,
.constraints-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.constraints-enter-from,
.constraints-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
