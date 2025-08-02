<template>
  <section class="container d-flex flex-column justify-content-center">
    <div class="row d-flex align-items-center justify-content-center">
      <div class="col-md-8 col-lg-7 col-xl-6">
        <img src="../assets/img/undraw_login.svg" class="img-fluid" alt="Login image">
      </div>
      <div class="col-md-7 col-lg-5 col-xl-5 offset-xl-1">
        <h1 class="mb-4 text-center">{{ $t("login") }}</h1>

        <div class="form-outline mb-4">
          <label class="form-label" for="input-email">{{ $t("email") }}</label>
          <input type="email" id="input-email" class="form-control form-control-lg" :placeholder="$t('email')" v-model="email" />
        </div>

        <div class="form-outline mb-4">
          <label class="form-label" for="input-password">{{ $t("password") }}</label>
          <input type="password" id="input-password" class="form-control form-control-lg"
                 :placeholder="$t('password')"
                 v-model="password"
                 @keyup.enter="loginStore.login"
          />
          <router-link :to="{ name: 'forgot-password' }">{{ $t("forgot-password") }}</router-link>
        </div>

        <div class="form-outline mb-1">
          <input
              type="button"
              class="btn btn-primary form-control form-control-lg"
              :value="$t('connect')"
              :disabled="!buttonEnabled"
              @click="loginStore.login"
          />
        </div>

        <router-link :to="{ name: 'create-account' }">{{ $t("create-account") }}</router-link>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">

import {useLoginStore} from "@/login/login.store";
import {storeToRefs} from "pinia";
import {onBeforeRouteLeave, useRoute} from "vue-router";

const loginStore = useLoginStore();
const { email, password, buttonEnabled } = storeToRefs(loginStore);

const route = useRoute();
loginStore.init(route.query?.activationToken as string | undefined);

onBeforeRouteLeave(loginStore.close);
</script>
