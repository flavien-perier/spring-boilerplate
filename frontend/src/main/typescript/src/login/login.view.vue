<template>
  <section class="container d-flex flex-column justify-content-center">
    <div class="row d-flex align-items-center justify-content-center">
      <div class="col-md-8 col-lg-7 col-xl-6">
        <img src="../assets/img/undraw_login.svg" class="img-fluid" alt="Login image">
      </div>
      <div class="col-md-7 col-lg-5 col-xl-5 offset-xl-1">
        <h1 class="mb-4 text-center">{{ $t("menu.login") }}</h1>

        <fio-input-email
            v-model="email"
            @update:isValid="value => isEmailValid = value"
        />

        <div class="form-outline mb-4">
          <fio-input-password
              v-model="password"
              :label="$t('field.password')"
              @keyup.enter="loginStore.login"
          />
          <router-link :to="{ name: 'forgot-password' }">{{ $t("action.forgot-password") }}</router-link>
        </div>

        <fio-input-button
            :label="$t('action.connect')"
            :disabled="!buttonEnabled"
            @click="loginStore.login"
        />

        <router-link :to="{ name: 'create-account' }">{{ $t("menu.create-account") }}</router-link>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import {useLoginStore} from "@/login/login.store";
import {storeToRefs} from "pinia";
import {onBeforeRouteLeave, useRoute} from "vue-router";

const loginStore = useLoginStore();
const { email, password, isEmailValid, buttonEnabled } = storeToRefs(loginStore);

const route = useRoute();
loginStore.init(route.query?.activationToken as string | undefined);

onBeforeRouteLeave(loginStore.close);
</script>
