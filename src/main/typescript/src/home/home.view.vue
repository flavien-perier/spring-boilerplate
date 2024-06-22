<template>
  <section class="container">
    <h1>{{ $t("home") }}</h1>

    <div class="mb-5">
      <img src="../assets/img/undraw_hello.svg" class="img-fluid d-block mx-auto" alt="Hello">
    </div>

    <div class="technologies-list">
      <div class="img-container">
        <template v-for="i in [0, 1]">
          <img src="../assets/img/logo.svg" alt="Logo">
          <img src="../assets/img/spring.svg" alt="Spring">
          <img src="../assets/img/kotlin.svg" alt="Kotlin">
          <img src="../assets/img/java.svg" alt="Java">
          <img src="../assets/img/vue.svg" alt="Vue">
          <img src="../assets/img/typescript.svg" alt="Typescript">
          <img src="../assets/img/openapi.svg" alt="OpenApi">
          <img src="../assets/img/docker.svg" alt="Docker">
          <img src="../assets/img/kubernetes.svg" alt="Kubernetes">
          <img src="../assets/img/helm.svg" alt="Helm">
        </template>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
@keyframes transition {
  from {
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

    img {
      height: 10rem;
      width: 10rem;
      margin-right: 2rem;
    }
  }
}
</style>