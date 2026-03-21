<template>
  <canvas ref="canvasRef" class="fio-qr-code" />
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from "vue";
import QRCode from "qrcode";

defineOptions({ name: "FioQrCode" });

const props = defineProps<{
  value: string;
}>();

const canvasRef = ref<HTMLCanvasElement | null>(null);

async function render() {
  if (canvasRef.value) {
    await QRCode.toCanvas(canvasRef.value, props.value);
  }
}

onMounted(render);
watch(() => props.value, render, { flush: "post" });
</script>

<style scoped lang="scss">
.fio-qr-code {
  display: block;
}
</style>
