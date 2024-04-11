<!-- src/App.vue -->

<template>
  <div>
    <router-view></router-view>
  </div>
</template>

<script setup lang="ts">
import { useDark } from "@vueuse/core";
localStorage.setItem("vueuse-color-scheme", "dark");
useDark();
</script>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}

.flex {
  display: flex;
}

.justify-center {
  justify-content: center;
}
</style>
