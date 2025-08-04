<template>
  <div class="row d-flex align-items-center justify-content-center">
    <div class="col-md-8 col-lg-7 col-xl-6">
      <img src="../../assets/img/undraw_updates.svg" class="img-fluid" alt="Update image">
    </div>
    <div class="col-md-7 col-lg-5 col-xl-5 offset-xl-1">
      <h2 class="mb-4 text-center">{{ $t("update-account") }}</h2>

      <input-email
          v-model="email"
          @update:isValid="value => isEmailValid = value"
      />

      <input-create-password
          v-model="password"
          @update:isValid="value => isPasswordValid = value"
          @keyup.enter="accountInformationStore.update"
      />

      <div class="form-outline mb-1">
        <input
            type="button"
            class="btn btn-primary form-control form-control-lg"
            :value="$t('update')"
            :disabled="!buttonEnabled"
            @click="accountInformationStore.update" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {useAccountInformationStore} from "@/account/account-information/account-information.store";
import {storeToRefs} from "pinia";
import {onBeforeRouteLeave} from "vue-router";
import InputEmail from "@/component-library/input/input-email.vue";
import InputCreatePassword from "@/component-library/input/input-create-password.vue";

const accountInformationStore = useAccountInformationStore();
const { email, password, buttonEnabled, isEmailValid, isPasswordValid } = storeToRefs(accountInformationStore);

accountInformationStore.init();

onBeforeRouteLeave(accountInformationStore.close);
</script>
