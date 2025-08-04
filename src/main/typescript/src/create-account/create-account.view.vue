<template>
  <section class="container d-flex flex-column justify-content-center">
    <div class="row d-flex align-items-center justify-content-center">
      <div class="col-md-8 col-lg-7 col-xl-6">
        <img src="../assets/img/undraw_create_account.svg" class="img-fluid" alt="Create account image">
      </div>
      <div class="col-md-7 col-lg-5 col-xl-5 offset-xl-1">
        <h1 class="mb-4 text-center">{{ $t("create-account") }}</h1>

        <input-email
            v-model="email"
            @update:isValid="value => isEmailValid = value"
        />

        <input-create-password
            v-model="password"
            @update:isValid="value => isPasswordValid = value"
        />

        <div class="form-outline mb-4">
          <input
              type="button"
              class="btn btn-primary form-control form-control-lg"
              :value="$t('create')"
              :disabled="!buttonEnabled"
              @click="createAccountStore.createAccount" />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import {useCreateAccountStore} from "@/create-account/create-account.store";
import {storeToRefs} from "pinia";
import {onBeforeRouteLeave} from "vue-router";
import InputCreatePassword from "@/component-library/input/input-create-password.vue";
import InputEmail from "@/component-library/input/input-email.vue";
const createAccountStore = useCreateAccountStore()

const { email, password, isPasswordValid, isEmailValid, buttonEnabled } = storeToRefs(createAccountStore);

createAccountStore.init();

onBeforeRouteLeave(createAccountStore.close);
</script>
