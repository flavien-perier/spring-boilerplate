<template>
  <section class="container d-flex flex-column justify-content-center">
    <div class="row d-flex align-items-center justify-content-center">
      <div class="col-md-8 col-lg-7 col-xl-6">
        <img src="../assets/img/undraw_authentication.svg" class="img-fluid" alt="Change password image">
      </div>
      <div class="col-md-7 col-lg-5 col-xl-5 offset-xl-1">
        <h1 class="mb-4 text-center">{{ $t("action.change-password") }}</h1>

        <fio-input-create-password
            v-model="password"
            :min-password-length="applicationStore.configuration.minPasswordLength"
            @update:isValid="value => isPasswordValid = value"
            @keyup.enter="changePasswordStore.update"
        />

        <fio-input-button
            :label="$t('action.update')"
            :disabled="!buttonEnabled"
            @click="changePasswordStore.update"
        />
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import {storeToRefs} from "pinia";
import {onBeforeRouteLeave, useRoute} from "vue-router";
import {useChangePasswordStore} from "@/change-password/change-password.store";
import {useApplicationStore} from "@/core/application.store";

const changePasswordStore = useChangePasswordStore();
const applicationStore = useApplicationStore();
const { password, buttonEnabled, isPasswordValid } = storeToRefs(changePasswordStore);

const route = useRoute();
changePasswordStore.init(
    route.query?.email as string | undefined,
    route.query?.forgotPasswordToken as string | undefined,
);

onBeforeRouteLeave(changePasswordStore.close);
</script>
