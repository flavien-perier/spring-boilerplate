<template>
  <h2 class="mb-s">{{ userMail }}</h2>
  <fio-tab-layout :tabs="userTabs" class="mb-l">
    <template v-if="activeTab === 'information'">
      <div v-if="user" class="user-information">
        <div class="mb-s">
          <strong>{{ $t("field.email") }} :</strong> {{ user.email }}
        </div>
        <div class="mb-s">
          <strong>{{ $t("field.otp") }} :</strong>
          {{
            user.otpEnabled
              ? $t("otp.status-active")
              : $t("otp.status-inactive")
          }}
        </div>
        <div class="mb-s">
          <fio-input-toggle v-model="enabled" :label="$t('field.enabled')" />
        </div>

        <h4 class="mt-l mb-s">{{ $t("action.update-account") }}</h4>
        <fio-input-text
          v-model="editEmail"
          :placeholder="$t('field.email')"
          class="mb-xs"
        />
        <fio-input-text
          v-model="editPassword"
          :placeholder="$t('field.password')"
          class="mb-xs"
        />
        <fio-input-text
          v-model="editProofOfWork"
          placeholder="Proof of Work"
          class="mb-xs"
        />
        <fio-input-button
          icon="floppy-disk"
          :label="$t('action.update')"
          :disabled="!editEmail"
          class="mb-s"
          @click="adminUserStore.updateUser()"
        />

        <h4 class="mt-l mb-s">{{ $t("field.actions") }}</h4>
        <fio-input-button
          icon="arrows-rotate"
          :label="$t('action.reset-otp')"
          :disabled="!user.otpEnabled"
          class="mb-xs"
          @click="adminUserStore.resetOtp()"
        />
        <fio-input-button
          icon="key"
          :label="$t('action.reset-password')"
          class="mb-xs"
          @click="adminUserStore.sendPasswordReset()"
        />
        <fio-input-button
          :label="$t('action.delete')"
          variant="danger"
          icon="trash"
          class="mb-xs"
          @click="adminUserStore.deleteUser()"
        />
      </div>
    </template>

    <template v-if="activeTab === 'rights'">
      <div class="user-rights">
        <h4 class="mb-s">{{ $t("menu.roles") }}</h4>
        <div v-for="role in roles" :key="role.id" class="mb-xs">
          <fio-input-checkbox
            :model-value="adminUserStore.isUserInRole(role.id)"
            :label="role.name"
            @change="adminUserStore.toggleUserRole(role.id)"
          />
        </div>

        <h4 class="mt-l mb-s">{{ $t("field.permission") }}</h4>
        <fio-input-tri-toggle
          v-for="setting in permissionOverrides"
          :key="setting.permission"
          v-model="triValues[setting.permission]"
          :label="setting.permission"
          :disabled="setting.locked"
          class="mb-xs"
          @change="onPermissionChange(setting.permission, $event)"
        />
      </div>
    </template>
  </fio-tab-layout>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import { computed, reactive, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { useAdminUserStore } from "@/admin/admin-user/admin-user.store";
import { storeToRefs } from "pinia";
import type { TabElement } from "@generated/component-library";

const { t } = useI18n();
const route = useRoute();

const userMail = route.params.userMail as string;

const activeTab = ref<string>("information");

const userTabs = computed<TabElement[]>(() => [
  {
    i18nKey: "tab.information",
    isActive: activeTab.value === "information",
    action: () => {
      activeTab.value = "information";
    },
  },
  {
    i18nKey: "tab.rights",
    isActive: activeTab.value === "rights",
    action: () => {
      activeTab.value = "rights";
    },
  },
]);

const adminUserStore = useAdminUserStore();
const {
  user,
  roles,
  userRoles,
  permissionOverrides,
  editEmail,
  editPassword,
  editProofOfWork,
} = storeToRefs(adminUserStore);

adminUserStore.init(userMail);

const enabled = computed<boolean>({
  get: () => adminUserStore.user?.enabled ?? false,
  set: (value: boolean) => adminUserStore.setEnabled(value),
});

const triValues = reactive<Record<string, boolean | null>>({});

watch(
  permissionOverrides,
  (newOverrides) => {
    newOverrides.forEach((setting) => {
      if (setting.locked) {
        triValues[setting.permission] = setting.inheritedAllow ?? false;
      } else {
        triValues[setting.permission] = setting.allow ?? null;
      }
    });
  },
  { immediate: true }
);

function onPermissionChange(permission: string, value: boolean | null) {
  if (value === null) {
    adminUserStore.removeUserPermission(permission);
  } else {
    adminUserStore.setUserPermission(permission, value);
  }
}
</script>

<style scoped lang="scss">
.user-information {
  max-width: 32rem;
}

.user-rights {
  max-width: 40rem;
}
</style>
