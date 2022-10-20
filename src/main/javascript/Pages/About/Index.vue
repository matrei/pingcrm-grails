<script>
import layout from '@/Shared/Layout.vue'
export default { layout }
</script>

<script setup>
const props = defineProps({
  appInfo: Object,
})
</script>

<template>
  <inertia-head title="About" />
  <h1 class="mb-8 font-bold text-3xl">About</h1>
  <ul>
    <li><strong>Name:</strong> {{ appInfo['name'] }}</li>
    <li><strong>Version:</strong> {{ appInfo['version'] }}</li>
    <li><strong>Grails version:</strong> {{ appInfo['grailsVersion'] }}</li>
    <li><strong>Environment:</strong> {{ appInfo['environment'] }}</li>
  </ul>
</template>

<style>
li { margin-bottom: 1rem }
</style>