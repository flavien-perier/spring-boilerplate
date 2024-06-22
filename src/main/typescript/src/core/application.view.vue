<template>
  <Navbar />

  <RouterView v-if="initOk" style="flex: auto" />
  <div v-if="!initOk" style="flex: auto" class="d-flex align-items-center justify-content-center">
    <div>
      <font-awesome-icon icon="spinner" size="6x" spin-pulse />
    </div>
  </div>

  <Modal />
  <Notification />
  <Footer />
</template>

<script setup lang="ts">
import Navbar from "@/core/component/navbar.component.vue";
import Footer from "@/core/component/footer.component.vue";
import Modal from "@/core/component/modal.component.vue";
import Notification from "@/core/component/notification.component.vue";

import { RouterView } from "vue-router";
import {useApplicationStore} from "@/core/application.store";
import {storeToRefs} from "pinia";

const applicationStore = useApplicationStore();
const { initOk } = storeToRefs(applicationStore);

applicationStore.init();
</script>
