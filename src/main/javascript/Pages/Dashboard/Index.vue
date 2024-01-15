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

<template>
  <inertia-head title="Dashboard" />
  <h1 class="mb-8 font-bold text-3xl">Dashboard</h1>
  <p class="mb-8 leading-normal">
    Hey there! Welcome to Ping CRM, a demo app designed to help illustrate how
    <a class="text-indigo-500 underline hover:text-orange-600" href="https://inertiajs.com">Inertia.js</a> works
    with <a class="text-indigo-500 underline hover:text-orange-600" href="https://grails.org">Grails</a>.
  </p>
  <div class="leading-[50px]">
    <inertia-link href="/test-500-error" class="btn-indigo">Trigger 500 Server Error</inertia-link>&nbsp;
    <inertia-link href="/static/test-404-error" class="btn-indigo">Trigger 404 Not Found</inertia-link>&nbsp;
    <inertia-link href="/hello" class="btn-indigo">Trigger 403 Forbidden</inertia-link>
  </div>
</template>