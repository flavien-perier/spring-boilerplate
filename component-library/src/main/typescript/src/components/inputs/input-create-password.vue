<template>
  <div class="form-outline mb-4">
    <input-password
        :label="$t('fio.password')"
        v-model="password"
        :is-valid="lengthValid && lowercaseValid && uppercaseValid && numberValid && specialCharValid"
        @focus="showPasswordInfo = true"
        @blur="showPasswordInfo = false"
    />

    <div
        class="position-absolute bg-white border rounded z-3 mt-2 ms-2 p-3"
        v-if="showPasswordInfo && (!lengthValid || !lowercaseValid || !uppercaseValid || !numberValid || !specialCharValid)"
    >
      <ul class="list-unstyled mb-0">
        <li>
          <fio-icon icon="circle-check" class="text-success" v-if="lengthValid" />
          <fio-icon icon="circle-xmark" class="text-danger" v-else />
          {{ $t("fio.password.constraint.length", { "minPasswordLength": props.minPasswordLength }) }}
        </li>
        <li>
          <fio-icon icon="circle-check" class="text-success" v-if="lowercaseValid" />
          <fio-icon icon="circle-xmark" class="text-danger" v-else />
          {{ $t("fio.password.constraint.lowercase") }}
        </li>
        <li>
          <fio-icon icon="circle-check" class="text-success" v-if="uppercaseValid" />
          <fio-icon icon="circle-xmark" class="text-danger" v-else />
          {{ $t("fio.password.constraint.uppercase") }}
        </li>
        <li>
          <fio-icon icon="circle-check" class="text-success" v-if="numberValid" />
          <fio-icon icon="circle-xmark" class="text-danger" v-else />
          {{ $t("fio.password.constraint.number") }}
        </li>
        <li>
          <fio-icon icon="circle-check" class="text-success" v-if="specialCharValid" />
          <fio-icon icon="circle-xmark" class="text-danger" v-else />
          {{ $t("fio.password.constraint.special-character") }}
        </li>
      </ul>
    </div>
  </div>

  <div class="form-outline mb-4">
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

defineOptions({
  name: "FioInputCreatePassword"
});

const props = defineProps({
  modelValue: { type: String, default: "" },
  minPasswordLength: { type: Number, default: 8 },
});

const emit = defineEmits(["update:modelValue", "update:isValid"]);

const password = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value)
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
  lengthValid.value = passwordUtil.checkPasswordLength(password.value, props.minPasswordLength);
  lowercaseValid.value = passwordUtil.checkPasswordLowercase(password.value);
  uppercaseValid.value = passwordUtil.checkPasswordUppercase(password.value);
  numberValid.value = passwordUtil.checkPasswordNumber(password.value);
  specialCharValid.value = passwordUtil.checkSpecialCharacter(password.value);
  passwordMatch.value = password.value == confirmPassword.value &&  confirmPassword.value !== "";
}

const isPasswordValid = computed(() => {
  return lengthValid.value && 
         lowercaseValid.value && 
         uppercaseValid.value && 
         numberValid.value && 
         specialCharValid.value && 
         passwordMatch.value;
});

watch(isPasswordValid, (newValue) => {
  emit("update:isValid", newValue);
});

onMounted(() => {
  validatePassword();
});

watch(() => props.modelValue, () => {
  validatePassword();
});
watch(() => props.minPasswordLength, () => {
  validatePassword();
});
</script>