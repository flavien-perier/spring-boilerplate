<template>
  <section class="container d-flex flex-column justify-content-center">
    <div class="row d-flex align-items-center justify-content-center">
      <div class="col-md-8 col-lg-7 col-xl-6">
        <img src="../assets/img/undraw_authentication.svg" class="img-fluid" alt="Change password image">
      </div>
      <div class="col-md-7 col-lg-5 col-xl-5 offset-xl-1">
        <h1 class="mb-4 text-center">{{ $t("change-password") }}</h1>

        <input-create-password
            v-model="password"
            @update:isValid="value => isPasswordValid = value"
            @keyup.enter="changePasswordStore.update"
        />

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
import InputCreatePassword from "@/component-library/input/input-create-password.vue";

const changePasswordStore = useChangePasswordStore();
const { password, buttonEnabled, isPasswordValid } = storeToRefs(changePasswordStore);

const route = useRoute();
changePasswordStore.init(
    route.query?.email as string | undefined,
    route.query?.forgotPasswordToken as string | undefined,
);

onBeforeRouteLeave(changePasswordStore.close);
</script>
