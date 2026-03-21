<template>
  <div class="row d-flex align-items-center justify-content-center">
    <div class="col-md-8 col-lg-7 col-xl-6">
      <img src="../../assets/img/undraw_updates.svg" class="img-fluid" alt="Update image">
    </div>
    <div class="col-md-7 col-lg-5 col-xl-5 offset-xl-1">
      <h2 class="mb-4 text-center">{{ $t("action.update-account") }}</h2>

      <fio-input-email
          v-model="email"
          @update:isValid="value => isEmailValid = value"
      />

      <fio-input-create-password
          v-model="password"
          :min-password-length="applicationStore.configuration.minPasswordLength"
          @update:isValid="value => isPasswordValid = value"
          @keyup.enter="accountInformationStore.update"
      />

      <fio-input-button
          :label="$t('action.update')"
          :disabled="!buttonEnabled"
          @click="accountInformationStore.update"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import {useAccountInformationStore} from "@/account/account-information/account-information.store";
import {useApplicationStore} from "@/core/application.store";
import {storeToRefs} from "pinia";
import {onBeforeRouteLeave} from "vue-router";

const accountInformationStore = useAccountInformationStore();
const applicationStore = useApplicationStore();
const { email, password, buttonEnabled, isEmailValid, isPasswordValid } = storeToRefs(accountInformationStore);

accountInformationStore.init();

onBeforeRouteLeave(accountInformationStore.close);
</script>
