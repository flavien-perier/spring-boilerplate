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
            :label="$t('action.update')"
            :disabled="!editEmail"
            class="mb-s"
            @click="adminUserStore.updateUser()"
          />

          <h4 class="mt-l mb-s">{{ $t("field.actions") }}</h4>
          <fio-input-button
            :label="$t('action.reset-otp')"
            :disabled="!user.otpEnabled"
            class="mb-xs"
            @click="adminUserStore.resetOtp()"
          />
          <fio-input-button
            :label="$t('action.reset-password')"
            class="mb-xs"
            @click="adminUserStore.sendPasswordReset()"
          />
          <fio-input-button
            :label="$t('action.delete')"
            variant="danger"
            class="mb-xs"
            @click="adminUserStore.deleteUser()"
          />
        </div>
      </template>

      <template v-if="activeTab === 'rights'">
        <div class="user-rights">
          <h4 class="mb-s">{{ $t("menu.groups") }}</h4>
          <div v-for="group in groups" :key="group.id" class="mb-xs">
            <fio-input-checkbox
              :model-value="adminUserStore.isUserInGroup(group.id)"
              :label="group.name"
              @change="adminUserStore.toggleUserGroup(group.id)"
            />
          </div>

          <h4 class="mt-l mb-s">{{ $t("field.permission") }}</h4>
          <fio-permission-setting
            v-for="setting in permissionOverrides"
            :key="setting.permission"
            v-model:override="overrideFlags[setting.permission]"
            v-model:allow="allowFlags[setting.permission]"
            :permission="setting.permission"
            :override-label="$t('field.override')"
            :allow-label="$t('field.allow')"
            :deny-label="$t('field.deny')"
            :locked="setting.locked"
            class="mb-xs"
            @change="onPermissionChange"
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
  groups,
  userGroups,
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

const overrideFlags = reactive<Record<string, boolean>>({});
const allowFlags = reactive<Record<string, boolean>>({});

watch(permissionOverrides, (newOverrides) => {
  newOverrides.forEach((setting) => {
    if (setting.locked) {
      overrideFlags[setting.permission] = true;
      allowFlags[setting.permission] = setting.inheritedAllow ?? false;
    } else {
      overrideFlags[setting.permission] =
        setting.allow !== undefined && setting.allow !== null;
      allowFlags[setting.permission] = setting.allow ?? false;
    }
  });
});

function onPermissionChange({
  permission,
  override,
  allow,
}: {
  permission: string;
  override: boolean;
  allow: boolean;
}) {
  if (override) {
    adminUserStore.setUserPermission(permission, allow);
  } else {
    adminUserStore.removeUserPermission(permission);
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
