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
  <div class="sm:grid lg:grid-flow-col grid-cols-2 lg:grid-cols-3 lg:grid-rows-2 gap-y-4 lg:gap-y-0 xl:w-3/4 2xl:w-3/5" style="grid-template-rows: auto auto">
    <h1 class="mb-4 sm:mb-6 lg:mb-6 font-bold text-3xl">About</h1>
    <div>
      <ul>
        <li><strong>App name:</strong> {{ info['name'] }}</li>
        <li><strong>App version:</strong> {{ info['version'] }}</li>
        <li><strong>Grails profile:</strong> {{ info['grailsProfile'] }}</li>
        <li><strong>Grails env:</strong> {{ info['grailsEnvironment'] }}</li>
        <li><strong>Grails version:</strong> {{ info['grailsVersion'] }}</li>
        <li><strong>Groovy version:</strong> {{ info['groovyVersion'] }}</li>
        <li><strong>JVM version:</strong> {{ info['javaVersion'] }}</li>
        <li><strong>SSR enabled:</strong> {{ info['ssrEnabled'] }}</li>
      </ul>
    </div>
    <h2 class="mt-8 sm:mt-1 mb-4 sm:mb-6 lg:mb-4 font-bold text-2xl">Artefacts</h2>
    <div>
      <ul>
        <li><strong>Controllers:</strong> {{ info['numControllers'] }}</li>
        <li><strong>Domains:</strong> {{ info['numDomains'] }}</li>
        <li><strong>Services:</strong> {{ info['numServices'] }}</li>
        <li><strong>Tag libraries:</strong> {{ info['numTagLibs'] }}</li>
      </ul>
    </div>
    <h2 class="mt-8 sm:mt-1 mb-4 sm:mb-6 lg:mb-4 font-bold text-2xl">Installed Plugins</h2>
    <div>
      <ul>
        <li v-for="plugin in info['plugins']" :key="plugin.name"><strong>{{ plugin.name }}:</strong> {{ plugin.version }}</li>
      </ul>
    </div>
  </div>
</template>

<style>
li { margin-bottom: 1rem }
</style>