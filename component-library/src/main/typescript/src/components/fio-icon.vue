<template>
  <font-awesome-icon v-bind="$attrs" :icon="icon" />
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import {
  faHome,
  faUser,
  faFlag,
  faGear,
  faTrash,
  faSpinner,
  faRightToBracket,
  faCircleCheck,
  faCircleXmark,
  faEye,
  faEyeSlash,
} from "@fortawesome/free-solid-svg-icons";

library.add(
  faHome,
  faUser,
  faFlag,
  faGear,
  faTrash,
  faSpinner,
  faRightToBracket,
  faCircleCheck,
  faCircleXmark,
  faEye,
  faEyeSlash,
);

defineOptions({
  name: "FioIcon",
  inheritAttrs: false,
});

defineProps({
  icon: { type: String, required: true },
});
</script>
