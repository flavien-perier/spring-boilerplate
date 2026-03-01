<template>
  <div class="notification-zone">
    <div
        v-for="notification in notifications"
        :key="notification.id"
        class="alert alert-info alert-dismissible fade show"
        :class="{
          'alert-info': notification.type === 'info',
          'alert-danger': notification.type === 'alert',
        }"
    >
      {{ notification.message }}
      <button class="btn-close" @click="applicationStore.closeNotification(notification.id)" />
    </div>
  </div>
</template>

<script setup lang="ts">
import {useApplicationStore} from "@/core/application.store";
import {storeToRefs} from "pinia";

const applicationStore = useApplicationStore();
const { notifications } = storeToRefs(applicationStore);
</script>

<style scoped lang="scss">
.notification-zone {
  position: absolute;
  width: 25vw;
  right: 2rem;
  top: 2rem
}
</style>
