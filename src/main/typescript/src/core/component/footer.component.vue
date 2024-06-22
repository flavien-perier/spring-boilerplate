<template>
  <footer class="bg-dark text-white text-center position-fixed bottom-0 w-100">
    <div class="text-center p-3">
      © {{ year }} Copyright:
      <a href="https://www.flavien.io/">flavien.io</a>
    </div>
  </footer>
</template>

<script setup lang="ts">
const year = new Date().getFullYear();
</script>