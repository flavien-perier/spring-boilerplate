<template>
  <font-awesome-icon
    :class="[
      'fio-icon',
      color && `fio-icon--${color}`,
      size && `fio-icon--${size}`,
      disabled && 'fio-icon--disabled',
      clickable && 'fio-icon--clickable',
    ]"
    :icon="icon"
    :spin="spin"
  />
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import {
  faAngleLeft,
  faAngleRight,
  faAnglesLeft,
  faAnglesRight,
  faArrowsRotate,
  faBold,
  faCircleCheck,
  faCircleXmark,
  faCode,
  faChevronDown,
  faDownload,
  faEye,
  faEyeSlash,
  faFlag,
  faFloppyDisk,
  faGear,
  faHeading,
  faHome,
  faItalic,
  faKey,
  faLink,
  faListUl,
  faMoon,
  faPaperPlane,
  faPlus,
  faQuoteRight,
  faRightToBracket,
  faShieldHalved,
  faSort,
  faSortDown,
  faSortUp,
  faSpinner,
  faSun,
  faTrash,
  faUser,
  faUserPlus,
  faUsers,
  faXmark,
} from "@fortawesome/free-solid-svg-icons";
import { computed } from "vue";
import type { IconColor } from "@/model/icon-color";
import type { IconSize } from "@/model/icon-size";

library.add(
  faHome,
  faUser,
  faUserPlus,
  faUsers,
  faFlag,
  faGear,
  faTrash,
  faSpinner,
  faRightToBracket,
  faCircleCheck,
  faCircleXmark,
  faChevronDown,
  faEye,
  faEyeSlash,
  faAngleLeft,
  faAngleRight,
  faAnglesLeft,
  faAnglesRight,
  faXmark,
  faSort,
  faSortDown,
  faSortUp,
  faMoon,
  faSun,
  faBold,
  faItalic,
  faHeading,
  faLink,
  faListUl,
  faCode,
  faQuoteRight,
  faArrowsRotate,
  faDownload,
  faFloppyDisk,
  faKey,
  faPaperPlane,
  faPlus,
  faShieldHalved
);

defineOptions({
  name: "FioIcon",
  inheritAttrs: false,
});

const { icon } = defineProps<{
  icon: string;
  color?: IconColor;
  size?: IconSize;
  disabled?: boolean;
  clickable?: boolean;
}>();

const spin = computed(() => icon === "spinner");
</script>

<style scoped lang="scss">
@use "../styles/variables-colors" as *;
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

.fio-icon--disabled {
  cursor: not-allowed;
}

.fio-icon--clickable {
  cursor: pointer;
  transition: color 0.2s ease;

  &:not(.fio-icon--disabled):hover {
    color: color-mix(in srgb, currentColor 70%, white);
  }
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
