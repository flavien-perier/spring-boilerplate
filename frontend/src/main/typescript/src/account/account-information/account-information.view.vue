<template>
  <fio-split-layout class="account-layout">
    <template #left>
      <div class="account-layout__image">
        <fio-image name="undraw_updates" alt="Update image" />
      </div>
    </template>

    <template #right>
      <div class="account-layout__content">
        <h2 class="account-layout__title mb-xl">
          {{ $t("action.update-account") }}
        </h2>

        <fio-input-email
          class="mb-xl"
          v-model="email"
          @update:isValid="(value) => (isEmailValid = value)"
        />

        <fio-input-create-password
          class="mb-xl"
          v-model="password"
          :min-password-length="
            applicationStore.configuration.minPasswordLength
          "
          @update:isValid="(value) => (isPasswordValid = value)"
          @keyup.enter="accountInformationStore.update"
        />

        <fio-input-button
          :label="$t('action.update')"
          :disabled="!buttonEnabled"
          :waiting="computeAction"
          @click="accountInformationStore.update"
        />
      </div>
    </template>
  </fio-split-layout>
</template>

<script setup lang="ts">
import { useAccountInformationStore } from "@/account/account-information/account-information.store";
import { useApplicationStore } from "@/core/application.store";
import { storeToRefs } from "pinia";
import { onBeforeRouteLeave } from "vue-router";

const accountInformationStore = useAccountInformationStore();
const applicationStore = useApplicationStore();
const { email, password, buttonEnabled, isEmailValid, isPasswordValid, computeAction } =
  storeToRefs(accountInformationStore);

accountInformationStore.init();

onBeforeRouteLeave(accountInformationStore.close);
</script>

<style scoped lang="scss">
.account-layout {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1rem;
  min-height: 100%;
}

.account-layout__image {
  display: none;

  @media (min-width: 768px) {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  :deep(.fio-image svg) {
    max-width: 100%;
    height: auto;
  }
}

.account-layout__content {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.account-layout__title {
  text-align: center;
}
</style>
