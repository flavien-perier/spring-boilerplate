<template>
  <div class="form-outline mb-4">
    <input-password
        :label="$t('password')"
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
          <font-awesome-icon icon="circle-check" class="text-success" v-if="lengthValid" />
          <font-awesome-icon icon="circle-xmark" class="text-danger" v-else />
          {{ $t("password.constraint.length", { "minPasswordLength": minPasswordLength }) }}
        </li>
        <li>
          <font-awesome-icon icon="circle-check" class="text-success" v-if="lowercaseValid" />
          <font-awesome-icon icon="circle-xmark" class="text-danger" v-else />
          {{ $t("password.constraint.lowercase") }}
        </li>
        <li>
          <font-awesome-icon icon="circle-check" class="text-success" v-if="uppercaseValid" />
          <font-awesome-icon icon="circle-xmark" class="text-danger" v-else />
          {{ $t("password.constraint.uppercase") }}
        </li>
        <li>
          <font-awesome-icon icon="circle-check" class="text-success" v-if="numberValid" />
          <font-awesome-icon icon="circle-xmark" class="text-danger" v-else />
          {{ $t("password.constraint.number") }}
        </li>
        <li>
          <font-awesome-icon icon="circle-check" class="text-success" v-if="specialCharValid" />
          <font-awesome-icon icon="circle-xmark" class="text-danger" v-else />
          {{ $t("password.constraint.special-character") }}
        </li>
      </ul>
    </div>
  </div>

  <div class="form-outline mb-4">
    <input-password
        :label="$t('repeat-password')"
        v-model="confirmPassword"
        :is-valid="passwordMatch"
        @input="validatePassword"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import { passwordUtil } from "@/core/util/password-util";
import { useApplicationStore } from "@/core/application.store";
import InputPassword from "@/component-library/input/input-password.vue";

defineOptions({
  name: "InputCreatePassword"
});

const { modelValue } = defineProps({
  modelValue: { type: String, default: "" },
});

const emit = defineEmits(["update:modelValue", "update:isValid"]);

const applicationStore = useApplicationStore();
const minPasswordLength = computed(() => applicationStore.configuration.minPasswordLength);

const password = computed({
  get: () => modelValue,
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
  lengthValid.value = passwordUtil.checkPasswordLength(password.value);
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

watch(() => modelValue, () => {
  validatePassword();
});
</script>