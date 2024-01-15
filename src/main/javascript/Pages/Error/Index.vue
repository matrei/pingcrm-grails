/*
* Copyright 2023 original authors
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

<script setup>

const props = defineProps({
  status: Number,
  description: String
})

const title = computed(() => {
  return {
    503: '503: Service Unavailable',
    500: '500: Server Error',
    404: '404: Page Not Found',
    403: '403: Forbidden',
  }[props.status]
})

const errorMsg = computed(() => {
  if (props.description) return props.description
  return {
    503: 'Sorry, we are doing some maintenance. Please check back soon.',
    500: 'Whoops, something went wrong on our servers.',
    404: 'Sorry, the page you are looking for could not be found.',
    403: 'Sorry, you are not authorized to access this page.',
  }[props.status]
})
</script>

<template>
  <inertia-head :title="title" />
  <div class="p-6 bg-indigo-800 min-h-screen flex justify-center items-center">
    <div class="w-full max-w-md">
      <logo class="block mx-auto w-full max-w-xs fill-white" height="50" />
      <div class="mt-8 bg-white rounded-lg shadow-xl overflow-hidden">
        <div class="px-10 py-12">
          <h1 class="text-center font-bold text-3xl">{{ title }}</h1>
          <div class="mx-auto mt-6 w-24 border-b-2 mb-6 text-center" />
          {{ errorMsg }}
        </div>
        <div class="px-10 py-4 bg-slate-100 border-t border-slate-100 flex gap-1 justify-center">
          <a href="#" onclick="history.back();return false;" class="btn-indigo" type="submit">Back</a>
          <inertia-link href="/" class="btn-indigo" type="submit">Home</inertia-link>
        </div>
      </div>
    </div>
  </div>
</template>