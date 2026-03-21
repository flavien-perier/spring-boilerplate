<template>
  <div class="row d-flex align-items-center justify-content-center">
    <div class="col-md-8 col-lg-7 col-xl-6">
      <img src="../../assets/img/undraw_security_on.svg" class="img-fluid" alt="Security image">
    </div>
    <div class="col-md-7 col-lg-5 col-xl-5 offset-xl-1">
      <h2 class="mb-4 text-center">{{ $t("menu.sessions") }}</h2>

      <table class="table">
        <thead>
        <tr>
          <th scope="col">{{ $t("field.date") }}</th>
          <th scope="col">{{ $t("field.actions") }}</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="session in sessions">
          <td>{{ dateUtil(session.creationDate) }}</td>
          <td><a class="link-offset-1 cursor-pointer" @click="accountSecurityStore.deleteSession(session.uuid)">
            <fio-icon icon="trash" />
            {{ $t("action.delete") }}
          </a></td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>

  <div class="d-flex justify-content-center mt-4">
    <fio-input-button variant="danger"
        :label="$t('action.delete-account')"
        @click="accountSecurityStore.deleteAccount"
    />
  </div>
</template>

<script setup lang="ts">
import {useAccountSecurityStore} from "@/account/account-security/account-security.store";
import {storeToRefs} from "pinia";
import {dateUtil} from "@/core/util/date-util";

const accountSecurityStore = useAccountSecurityStore();
const { sessions } = storeToRefs(accountSecurityStore);

accountSecurityStore.init();
</script>
