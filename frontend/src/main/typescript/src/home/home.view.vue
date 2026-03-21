<template>
  <section class="home-view">
    <h1>{{ $t("menu.home") }}</h1>

    <fio-image name="undraw_hello" class="home-view__logo" />

    <div class="technologies-list">
      <div class="img-container">
        <template v-for="i in [0, 1]">
          <fio-image name="logo" />
          <fio-image name="spring" />
          <fio-image name="kotlin" />
          <fio-image name="java" />
          <fio-image name="vue" />
          <fio-image name="typescript" />
          <fio-image name="openapi" />
          <fio-image name="docker" />
          <fio-image name="kubernetes" />
          <fio-image name="helm" />
        </template>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
.home-view {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 1rem;
}

.home-view__logo {
  display: block;
  max-width: 100%;
  margin: 0 auto 3rem;

  :deep(svg) {
    display: block;
    max-width: 100%;
    height: auto;
  }
}

@keyframes transition {
  from {
    /* 10 images × (10rem width + 2rem margin-right) = 120rem — must match fio-image dimensions below */
    margin-left: -120rem;
  }
  to {
    margin-left: 0;
  }
}
.technologies-list {
  overflow: hidden;
  .img-container {
    display: flex;
    animation: transition 40s linear infinite;

    :deep(.fio-image) {
      height: 10rem;
      width: 10rem;
      flex-shrink: 0;
      margin-right: 2rem;

      svg {
        width: 100%;
        height: 100%;
      }
    }
  }
}
</style>
