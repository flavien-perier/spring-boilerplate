<template>
  <section class="container d-flex flex-column justify-content-center">
    <div class="row d-flex align-items-center justify-content-center">
      <div class="col-md-8 col-lg-7 col-xl-6">
        <img src="../assets/img/undraw_create_account.svg" class="img-fluid" alt="Create account image">
      </div>
      <div class="col-md-7 col-lg-5 col-xl-5 offset-xl-1">
        <h1 class="mb-4 text-center">{{ $t("create-account") }}</h1>

        <div class="form-outline mb-4">
          <label class="form-label" for="input-email">{{ $t("email") }}</label>
          <input type="email" id="input-email" class="form-control form-control-lg" :placeholder="$t('email')" v-model="email" />
        </div>

        <div class="form-outline mb-4">
          <label class="form-label" for="input-password">{{ $t("password") }}</label>
          <input type="password" id="input-password" class="form-control form-control-lg" :placeholder="$t('password')" v-model="password" />
        </div>

        <div class="form-outline mb-4">
          <label class="form-label" for="input-repeat-password">{{ $t("repeat-password") }}</label>
          <input type="password" id="input-repeat-password" class="form-control form-control-lg" :placeholder="$t('repeat-password')" v-model="repeatPassword" />
        </div>

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

const createAccountStore = useCreateAccountStore();
const { email, password, repeatPassword, buttonEnabled } = storeToRefs(createAccountStore);

createAccountStore.init();

onBeforeRouteLeave(createAccountStore.close);
</script>
