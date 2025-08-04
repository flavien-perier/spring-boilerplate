<template>
  <section class="container d-flex flex-column justify-content-center">
    <div class="row d-flex align-items-center justify-content-center">
      <div class="col-md-8 col-lg-7 col-xl-6">
        <img src="../assets/img/undraw_forgot_password.svg" class="img-fluid" alt="Forgot password image">
      </div>
      <div class="col-md-7 col-lg-5 col-xl-5 offset-xl-1">
        <h1 class="mb-4 text-center">{{ $t("forgot-password") }}</h1>

        <input-email
            v-model="email"
            @update:isValid="value => isEmailValid = value"
            @keyup.enter="forgotPasswordStore.send"
        />

        <div class="form-outline mb-4">
          <input
              type="button"
              class="btn btn-primary form-control form-control-lg"
              :value="$t('send')"
              :disabled="!buttonEnabled"
              @click="forgotPasswordStore.send" />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import {useForgotPasswordStore} from "@/forgot-password/forgot-password.store";
import {storeToRefs} from "pinia";
import {onBeforeRouteLeave} from "vue-router";
import InputEmail from "@/component-library/input/input-email.vue";

const forgotPasswordStore = useForgotPasswordStore();
const { email, buttonEnabled, isEmailValid } = storeToRefs(forgotPasswordStore);

forgotPasswordStore.init();

onBeforeRouteLeave(forgotPasswordStore.close);
</script>
