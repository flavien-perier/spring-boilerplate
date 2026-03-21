<template>
  <div class="account-layout">
    <div class="account-layout__image">
      <fio-image name="undraw_updates" alt="Update image" />
    </div>
    <div class="account-layout__content">
      <h2 class="account-layout__title">{{ $t("action.update-account") }}</h2>

      <fio-input-email
        class="mb-xl"
        v-model="email"
        @update:isValid="(value) => (isEmailValid = value)"
      />

      <fio-input-create-password
        class="mb-xl"
        v-model="password"
        :min-password-length="applicationStore.configuration.minPasswordLength"
        @update:isValid="(value) => (isPasswordValid = value)"
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
import { useAccountInformationStore } from "@/account/account-information/account-information.store";
import { useApplicationStore } from "@/core/application.store";
import { storeToRefs } from "pinia";
import { onBeforeRouteLeave } from "vue-router";

const accountInformationStore = useAccountInformationStore();
const applicationStore = useApplicationStore();
const { email, password, buttonEnabled, isEmailValid, isPasswordValid } =
  storeToRefs(accountInformationStore);

accountInformationStore.init();

onBeforeRouteLeave(accountInformationStore.close);
</script>

<style scoped lang="scss">
.account-layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
  max-width: 1400px;
  margin: 0 auto;
  padding: 1rem;
  min-height: 100%;
  align-items: center;

  @media (min-width: 768px) {
    grid-template-columns: 8fr 7fr;
  }

  @media (min-width: 992px) {
    grid-template-columns: 7fr 5fr;
  }

  @media (min-width: 1200px) {
    grid-template-columns: 6fr 5fr;
  }
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
  margin-bottom: 1.5rem;
}
</style>
