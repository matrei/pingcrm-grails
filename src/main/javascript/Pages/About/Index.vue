/*
* Copyright 2022-2024 original authors
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
import { relativeTime } from '@/Use/use-i18n'

const props = defineProps({
  info: Object,
})

const relativeCommitTime = computed(() => relativeTime(props.info['git.commitTime']))
</script>

<template>
  <inertia-head :title="$t('ui.about.title')" />
  <div class="sm:grid lg:grid-flow-col grid-cols-2 lg:grid-cols-3 lg:grid-rows-2 gap-y-4 lg:gap-y-0 xl:w-3/4 2xl:w-3/5" style="grid-template-rows: auto auto">
    <h1 class="mb-4 sm:mb-6 lg:mb-6 font-bold text-3xl">{{ $t('ui.about.title') }}</h1>
    <div>
      <ul>
        <li><strong>{{ $t('ui.about.appName') }}:</strong> {{ info['name'] }}</li>
        <li><strong>{{ $t('ui.about.appVersion') }}:</strong> {{ info['version'] }}</li>
        <li><strong>{{ $t('ui.about.gitCommit') }}:</strong> <a :href="info['git.commitUrl']" class="underline">{{ info['git.commitSha'] }}</a> ({{ relativeCommitTime }})</li>
        <li><strong>{{ $t('ui.about.grailsEnv') }}:</strong> {{ info['grailsEnvironment'] }}</li>
        <li><strong>{{ $t('ui.about.grailsVersion') }}:</strong> {{ info['grailsVersion'] }}</li>
        <li><strong>{{ $t('ui.about.groovyVersion') }}:</strong> {{ info['groovyVersion'] }}</li>
        <li><strong>{{ $t('ui.about.jvmVersion') }}:</strong> {{ info['javaVersion'] }}</li>
        <li><strong>{{ $t('ui.about.ssrEnabled') }}:</strong> {{ info['ssrEnabled'] }}</li>
      </ul>
    </div>
    <h2 class="mt-8 sm:mt-1 mb-4 sm:mb-6 lg:mb-4 font-bold text-2xl">{{ $t('ui.about.artefacts') }}</h2>
    <div>
      <ul>
        <li><strong>{{ $t('ui.about.controllers') }}:</strong> {{ info['numControllers'] }}</li>
        <li><strong>{{ $t('ui.about.domains') }}:</strong> {{ info['numDomains'] }}</li>
        <li><strong>{{ $t('ui.about.services') }}:</strong> {{ info['numServices'] }}</li>
        <li><strong>{{ $t('ui.about.tagLibs') }}:</strong> {{ info['numTagLibs'] }}</li>
      </ul>
    </div>
    <h2 class="mt-8 sm:mt-1 mb-4 sm:mb-6 lg:mb-4 font-bold text-2xl">{{ $t('ui.about.plugins') }}</h2>
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