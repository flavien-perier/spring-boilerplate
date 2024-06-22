<template>
  <section class="container d-flex flex-column justify-content-center">
    <div class="row d-flex align-items-center justify-content-center">
      <div class="col-md-8 col-lg-7 col-xl-6">
        <img src="../assets/img/undraw_authentication.svg" class="img-fluid" alt="Change password image">
      </div>
      <div class="col-md-7 col-lg-5 col-xl-5 offset-xl-1">
        <h1 class="mb-4 text-center">{{ $t("change-password") }}</h1>

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
              :value="$t('update')"
              :disabled="!buttonEnabled"
              @click="changePasswordStore.update" />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import {storeToRefs} from "pinia";
import {onBeforeRouteLeave, useRoute} from "vue-router";
import {useChangePasswordStore} from "@/change-password/change-password.store";

const changePasswordStore = useChangePasswordStore();
const { password, repeatPassword, buttonEnabled } = storeToRefs(changePasswordStore);

const route = useRoute();
changePasswordStore.init(
    route.query?.email as string | undefined,
    route.query?.forgotPasswordToken as string | undefined,
);

onBeforeRouteLeave(changePasswordStore.close);
</script>
