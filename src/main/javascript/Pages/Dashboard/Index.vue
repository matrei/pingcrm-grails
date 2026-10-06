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
import { ErrorTestController } from '@/routes'
</script>

<template>
  <inertia-head :title="$t('ui.dashboard.title')" />
  <h1 class="mb-8 font-bold text-3xl">{{ $t('ui.dashboard.title') }}</h1>
  <p class="mb-8 leading-normal">
    {{ $t('ui.dashboard.welcome.beforeInertia') }}
    <a class="text-indigo-500 underline hover:text-orange-600" href="https://inertiajs.com">Inertia.js</a>
    {{ $t('ui.dashboard.welcome.beforeGrails') }}
    <a class="text-indigo-500 underline hover:text-orange-600" href="https://grails.org">Grails</a>{{ $t('ui.dashboard.welcome.end') }}
  </p>
  <div class="leading-[50px]">
    <inertia-link :href="ErrorTestController.generateInternalServerError()" class="btn-indigo">{{ $t('ui.dashboard.trigger500') }}</inertia-link>&nbsp;
    <inertia-link href="/static/test-404-error" class="btn-indigo">{{ $t('ui.dashboard.trigger404') }}</inertia-link>&nbsp;
    <inertia-link href="/hello" class="btn-indigo">{{ $t('ui.dashboard.trigger403') }}</inertia-link>
  </div>
</template>