<template>
  <div class="account-layout">
    <div class="account-layout__image">
      <fio-image name="undraw_security_on" alt="Security image" />
    </div>
    <div class="account-layout__content">
      <h2 class="account-layout__title">{{ $t("menu.sessions") }}</h2>

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
    </div>
  </div>

  <div class="account-layout__danger-zone">
    <fio-input-button
      variant="danger"
      :label="$t('action.delete-account')"
      @click="accountSecurityStore.deleteAccount"
    />
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
const { sessions } = storeToRefs(accountSecurityStore);

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

.account-layout__danger-zone {
  display: flex;
  justify-content: center;
  margin-top: 1.5rem;
}
</style>
