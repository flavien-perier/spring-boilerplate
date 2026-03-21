<template>
  <fio-modal
    :show="modal.show"
    :title="$t(modal.title)"
    :content="$t(modal.content)"
    :reject-message="modal.rejectMessage ? $t(modal.rejectMessage) : ''"
    :resolve-message="modal.resolveMessage ? $t(modal.resolveMessage) : ''"
    @close="applicationStore.closeModal"
    @resolve="applicationStore.validateModal"
  />
</template>

<script setup lang="ts">
import { useApplicationStore } from "@/core/application.store";
import { storeToRefs } from "pinia";

const applicationStore = useApplicationStore();
const { modal } = storeToRefs(applicationStore);
</script>
