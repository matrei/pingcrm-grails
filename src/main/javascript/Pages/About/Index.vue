<script>
import layout from '@/Shared/Layout.vue'
export default { layout }
</script>

<script setup>
const props = defineProps({
  info: Object,
})
</script>

<template>
  <inertia-head title="About" />
  <h1 class="mb-8 font-bold text-3xl">About</h1>
  <ul>
    <li><strong>App name:</strong> {{ info['name'] }}</li>
    <li><strong>App version:</strong> {{ info['version'] }}</li>
    <li><strong>Grails profile:</strong> {{ info['grailsProfile'] }}</li>
    <li><strong>Grails env:</strong> {{ info['grailsEnvironment'] }}</li>
    <li><strong>Grails version:</strong> {{ info['grailsVersion'] }}</li>
    <li><strong>Groovy version:</strong> {{ info['groovyVersion'] }}</li>
    <li><strong>Java version:</strong> {{ info['javaVersion'] }}</li>
  </ul>
</template>

<style>
li { margin-bottom: 1rem }
</style>