<template>
  <div class="account-layout">
    <div class="account-layout__content">
      <section class="account-section">
        <h2 class="account-section__title mb-xl">{{ $t("menu.sessions") }}</h2>

        <fio-table :headers="headers">
          <template #body>
            <tr v-for="session in sessions" :key="session.uuid">
              <td>{{ dateUtil(session.creationDate) }}</td>
              <td>
                <a
                  class="link-offset-1 cursor-pointer"
                  @click="accountSecurityStore.deleteSession(session.uuid)"
                >
                  <fio-icon icon="trash" />
                  {{ $t("action.delete") }}
                </a>
              </td>
            </tr>
          </template>
        </fio-table>
      </section>

      <section class="account-section">
        <h2 class="account-section__title mb-xl">{{ $t("menu.security") }}</h2>

        <template v-if="!otpEnabled && !otpSetupUri">
          <p class="text-center">{{ $t("otp.status-inactive") }}</p>
          <fio-input-button
            :label="$t('action.enable-otp')"
            @click="accountSecurityStore.setupOtp"
          />
        </template>

        <template v-else-if="otpSetupUri">
          <p class="text-center">{{ $t("otp.scan-qr") }}</p>
          <div class="otp-canvas-wrapper mt mb">
            <fio-qr-code :value="otpSetupUri" />
          </div>
          <p class="text-center text-muted">{{ $t("otp.confirm-hint") }}</p>
          <fio-input-text
            class="mb-xl"
            v-model="otpCode"
            :label="$t('field.otp')"
            :placeholder="$t('field.otp')"
            :max-length="6"
            :allowed-characters="/\d/"
          />
          <fio-input-button
            :label="$t('action.confirm-otp')"
            :disabled="otpCode.length !== 6"
            @click="accountSecurityStore.confirmOtp"
          />
        </template>

        <template v-else>
          <fio-input-button
            variant="danger"
            :label="$t('action.disable-otp')"
            @click="accountSecurityStore.disableOtp"
          />
        </template>
      </section>

      <div class="account-layout__danger-zone">
        <fio-input-button
          variant="danger"
          :label="$t('action.delete-account')"
          @click="accountSecurityStore.deleteAccount"
        />
      </div>
    </div>

    <div class="account-layout__image">
      <fio-image name="undraw_security_on" alt="Security image" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import { computed } from "vue";
import { useAccountSecurityStore } from "@/account/account-security/account-security.store";
import { storeToRefs } from "pinia";
import { dateUtil } from "@/core/util/date-util";
import type { TableHeader } from "@generated/component-library";

const { t } = useI18n();

const headers = computed<TableHeader[]>(() => [
  { name: t("field.date"), position: 0, sortable: false, show: true },
  { name: t("field.actions"), position: 1, sortable: false, show: true },
]);

const accountSecurityStore = useAccountSecurityStore();
const { sessions, otpEnabled, otpSetupUri, otpCode } =
  storeToRefs(accountSecurityStore);

accountSecurityStore.init();
</script>

<style scoped lang="scss">
.account-layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
  max-width: 1400px;
  margin: 0 auto;
  padding: 1rem;
  align-items: start;

  @media (min-width: 768px) {
    grid-template-columns: 7fr 5fr;
    align-items: center;
  }
}

.account-layout__content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.account-layout__image {
  display: none;

  @media (min-width: 768px) {
    display: flex;
    align-items: center;
    justify-content: center;
    position: sticky;
    top: 2rem;
  }

  :deep(.fio-image svg) {
    max-width: 100%;
    height: auto;
  }
}

.account-layout__danger-zone {
  display: flex;
  justify-content: center;
}

.account-section {
  display: flex;
  flex-direction: column;
}

.account-section__title {
  text-align: center;
}

.otp-canvas-wrapper {
  display: flex;
  justify-content: center;
}
</style>
