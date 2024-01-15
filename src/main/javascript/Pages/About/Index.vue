/*
* Copyright 2022-2023 original authors
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

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