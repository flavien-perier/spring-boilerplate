<template>
  <section class="container d-flex flex-column justify-content-center">
    <div class="row d-flex align-items-center justify-content-center">
      <div class="col-md-8 col-lg-7 col-xl-6">
        <img src="../assets/img/undraw_create_account.svg" class="img-fluid" alt="Create account image">
      </div>
      <div class="col-md-7 col-lg-5 col-xl-5 offset-xl-1">
        <h1 class="mb-4 text-center">{{ $t("menu.create-account") }}</h1>

        <fio-input-email
            v-model="email"
            @update:isValid="value => isEmailValid = value"
        />

        <fio-input-create-password
            v-model="password"
            :min-password-length="applicationStore.configuration.minPasswordLength"
            @update:isValid="value => isPasswordValid = value"
        />

        <fio-input-button
            :label="$t('action.create')"
            :disabled="!buttonEnabled"
            @click="createAccountStore.createAccount"
        />
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import {useCreateAccountStore} from "@/create-account/create-account.store";
import {useApplicationStore} from "@/core/application.store";
import {storeToRefs} from "pinia";
import {onBeforeRouteLeave} from "vue-router";
const createAccountStore = useCreateAccountStore()
const applicationStore = useApplicationStore()

const { email, password, isPasswordValid, isEmailValid, buttonEnabled } = storeToRefs(createAccountStore);

createAccountStore.init();

onBeforeRouteLeave(createAccountStore.close);
</script>
