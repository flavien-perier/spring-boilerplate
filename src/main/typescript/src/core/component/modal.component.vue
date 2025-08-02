<template>
  <div class="modal" :class="{ 'd-flex': modal.show }">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ $t(modal.title) }}</h5>
          <button class="btn-close" @click="applicationStore.closeModal" />
        </div>
        <div class="modal-body">
          <p>{{ $t(modal.content) }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" v-if="modal.rejectMessage" @click="applicationStore.closeModal">
            {{ $t(modal.rejectMessage) }}
          </button>
          <button class="btn btn-primary" v-if="modal.resolveMessage" @click="applicationStore.validateModal">
            {{ $t(modal.resolveMessage) }}
          </button>
        </div>
      </div>
    </div>
  </div>

</template>

<script setup lang="ts">
import {useApplicationStore} from "@/core/application.store";
import {storeToRefs} from "pinia";

const applicationStore = useApplicationStore();
const { modal } = storeToRefs(applicationStore);
</script>

<style scoped lang="scss">
.modal {
  align-items: center;
  background-color: #aaaaaa20;
  backdrop-filter: blur(5px);

  .modal-content {
    width: 30vw;
  }
}
</style>
