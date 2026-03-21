<template>
  <div class="fio-create-password">
    <div class="password-field">
      <input-password
        :label="$t('fio.password')"
        v-model="password"
        :is-valid="
          lengthValid &&
          lowercaseValid &&
          uppercaseValid &&
          numberValid &&
          specialCharValid
        "
        @focus="showPasswordInfo = true"
        @blur="showPasswordInfo = false"
      />

      <Transition name="constraints">
        <div
          class="password-constraints"
          v-if="
            showPasswordInfo &&
            (!lengthValid ||
              !lowercaseValid ||
              !uppercaseValid ||
              !numberValid ||
              !specialCharValid)
          "
        >
          <ul class="constraints-list">
            <li>
              <fio-icon
                v-if="lengthValid"
                icon="circle-check"
                color="success"
              />
              <fio-icon v-else icon="circle-xmark" color="danger" />
              {{
                $t("fio.password.constraint.length", {
                  minPasswordLength: props.minPasswordLength,
                })
              }}
            </li>
            <li>
              <fio-icon
                v-if="lowercaseValid"
                icon="circle-check"
                color="success"
              />
              <fio-icon v-else icon="circle-xmark" color="danger" />
              {{ $t("fio.password.constraint.lowercase") }}
            </li>
            <li>
              <fio-icon
                v-if="uppercaseValid"
                icon="circle-check"
                color="success"
              />
              <fio-icon v-else icon="circle-xmark" color="danger" />
              {{ $t("fio.password.constraint.uppercase") }}
            </li>
            <li>
              <fio-icon
                v-if="numberValid"
                icon="circle-check"
                color="success"
              />
              <fio-icon v-else icon="circle-xmark" color="danger" />
              {{ $t("fio.password.constraint.number") }}
            </li>
            <li>
              <fio-icon
                v-if="specialCharValid"
                icon="circle-check"
                color="success"
              />
              <fio-icon v-else icon="circle-xmark" color="danger" />
              {{ $t("fio.password.constraint.special-character") }}
            </li>
          </ul>
        </div>
      </Transition>
    </div>

    <input-password
      :label="$t('fio.repeat-password')"
      v-model="confirmPassword"
      :is-valid="passwordMatch"
      @input="validatePassword"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import { passwordUtil } from "../../utils/password-util";
import InputPassword from "./input-password.vue";
import FioIcon from "../fio-icon.vue";

defineOptions({
  name: "FioInputCreatePassword",
});

const props = defineProps({
  modelValue: { type: String, default: "" },
  minPasswordLength: { type: Number, default: 8 },
});

const emit = defineEmits(["update:modelValue", "update:isValid"]);

const password = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value),
});
const confirmPassword = ref("");

const lengthValid = ref(false);
const lowercaseValid = ref(false);
const uppercaseValid = ref(false);
const numberValid = ref(false);
const specialCharValid = ref(false);
const passwordMatch = ref(false);

const showPasswordInfo = ref(false);

function validatePassword() {
  lengthValid.value = passwordUtil.checkPasswordLength(
    password.value,
    props.minPasswordLength
  );
  lowercaseValid.value = passwordUtil.checkPasswordLowercase(password.value);
  uppercaseValid.value = passwordUtil.checkPasswordUppercase(password.value);
  numberValid.value = passwordUtil.checkPasswordNumber(password.value);
  specialCharValid.value = passwordUtil.checkSpecialCharacter(password.value);
  passwordMatch.value =
    password.value == confirmPassword.value && confirmPassword.value !== "";
}

const isPasswordValid = computed(() => {
  return (
    lengthValid.value &&
    lowercaseValid.value &&
    uppercaseValid.value &&
    numberValid.value &&
    specialCharValid.value &&
    passwordMatch.value
  );
});

watch(isPasswordValid, (newValue) => {
  emit("update:isValid", newValue);
});

onMounted(() => {
  validatePassword();
});

watch(
  () => props.modelValue,
  () => {
    validatePassword();
  }
);
watch(
  () => props.minPasswordLength,
  () => {
    validatePassword();
  }
);
</script>

<style scoped lang="scss">
@use "@/styles/variables" as *;
@use "@/styles/colors" as *;

.password-field {
  position: relative;
  margin-bottom: $margin-xl;
}

.password-constraints {
  position: absolute;
  background-color: $secondary-lighter-90;
  border: $border-size solid $secondary-lighter-80;
  border-radius: $border-radius-size;
  box-shadow: 0 4px 12px rgba($secondary-darker-90, 0.1);
  z-index: 3;
  margin-top: calc(-#{$margin-xl} + #{$margin-xxs});
  padding: $margin-s $margin;
  min-width: 16rem;
}

.constraints-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: $margin-xs;

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
