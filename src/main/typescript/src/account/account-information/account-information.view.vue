<template>
  <div class="row d-flex align-items-center justify-content-center">
    <div class="col-md-8 col-lg-7 col-xl-6">
      <img src="../../assets/img/undraw_updates.svg" class="img-fluid" alt="Update image">
    </div>
    <div class="col-md-7 col-lg-5 col-xl-5 offset-xl-1">
      <h2 class="mb-4 text-center">{{ $t("update-account") }}</h2>

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
        <input type="password" id="input-repeat-password" class="form-control form-control-lg"
               :placeholder="$t('repeat-password')"
               v-model="repeatPassword"
               @keyup.enter="accountInformationStore.update"
        />
      </div>

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

const accountInformationStore = useAccountInformationStore();
const { email, password, repeatPassword, buttonEnabled } = storeToRefs(accountInformationStore);

accountInformationStore.init();

onBeforeRouteLeave(accountInformationStore.close);
</script>
