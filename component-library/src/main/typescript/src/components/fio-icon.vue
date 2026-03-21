<template>
  <font-awesome-icon
    :class="[
      'fio-icon',
      color && `fio-icon--${color}`,
      size && `fio-icon--${size}`,
    ]"
    :icon="icon"
    :spin="spin"
  />
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
  faAngleLeft,
  faAngleRight,
  faXmark,
  faSort,
} from "@fortawesome/free-solid-svg-icons";
import { computed } from "vue";
import type { IconColor } from "@/model/icon-color";
import type { IconSize } from "@/model/icon-size";

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
  faAngleLeft,
  faAngleRight,
  faXmark,
  faSort
);

defineOptions({
  name: "FioIcon",
  inheritAttrs: false,
});

const { icon } = defineProps<{
  icon: string;
  color?: IconColor;
  size?: IconSize;
}>();

const spin = computed(() => icon === "spinner");
</script>

<style scoped lang="scss">
@use "../styles/colors" as *;
@use "../styles/variables" as *;

.fio-icon {
  display: inline-flex;
  align-items: center;
  font-size: $font-size;
}

.fio-icon--primary {
  color: $primary;
}

.fio-icon--secondary {
  color: $secondary;
}

.fio-icon--info {
  color: $info;
}

.fio-icon--success {
  color: $success;
}

.fio-icon--warning {
  color: $warning;
}

.fio-icon--danger {
  color: $danger;
}

.fio-icon--s {
  font-size: $font-s-size;
}

.fio-icon--l {
  font-size: $font-l-size;
}

.fio-icon--xl {
  font-size: $font-xl-size;
}

.fio-icon--xxl {
  font-size: $font-xxl-size;
}
</style>
