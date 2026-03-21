<template>
  <div class="notification-zone">
    <fio-alert
      v-for="notification in notifications"
      :key="notification.id"
      :type="notification.type"
      :message="notification.message"
      @close="applicationStore.closeNotification(notification.id)"
    />
  </div>
</template>

<script setup lang="ts">
import { useApplicationStore } from "@/core/application.store";
import { storeToRefs } from "pinia";

const applicationStore = useApplicationStore();
const { notifications } = storeToRefs(applicationStore);
</script>

<style scoped lang="scss">
.notification-zone {
  position: absolute;
  width: 25vw;
  right: 2rem;
  top: 2rem;
}
</style>
