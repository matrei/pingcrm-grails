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

<script setup>

defineProps({
  auth: { type: Object, default: () => ({}) }
})

</script>

<template>
  <!-- Note: leading-none is set on body but I had to add it somewhere in a component or else it wouldn't make it into the final CSS -->
  <div class="leading-none">
    <div class="md:flex md:flex-col">
      <div class="md:h-screen md:flex md:flex-col">
        <div class="md:flex md:shrink-0">
          <div class="bg-indigo-900 md:shrink-0 md:w-56 px-6 py-4 flex items-center justify-between md:justify-center">
            <inertia-link class="mt-1" href="/">
              <logo class="fill-white" width="120" height="28" />
            </inertia-link>
            <dropdown class="md:hidden" placement="bottom-end">
              <svg class="fill-white w-6 h-6" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><path d="M0 3h20v2H0V3zm0 6h20v2H0V9zm0 6h20v2H0v-2z" /></svg>
              <template #dropdown>
                <div class="mt-2 px-8 py-4 shadow-lg bg-indigo-800 rounded">
                  <main-menu />
                </div>
              </template>
            </dropdown>
          </div>
          <div class="bg-white border-b w-full p-4 md:py-0 md:px-12 text-sm md:text-md flex justify-between items-center">
            <div class="mt-1 mr-4">{{ auth.user.account.name }}</div>
            <dropdown class="mt-1" placement="bottom-end">
              <div class="flex items-center cursor-pointer select-none group">
                <div class="text-slate-700 group-hover:text-indigo-600 focus:text-indigo-600 mr-1 whitespace-nowrap">
                  <span>{{ auth.user.first_name }}</span>
                  <span class="hidden md:inline">&nbsp;{{ auth.user.last_name }}</span>
                </div>
                <icon class="w-5 h-5 group-hover:fill-indigo-600 fill-slate-700 focus:fill-indigo-600" name="cheveron-down" />
              </div>
              <template #dropdown>
                <div class="mt-2 py-2 shadow-xl bg-white rounded text-sm">
                  <inertia-link class="block px-6 py-2 hover:bg-indigo-500 hover:text-white" :href="`/users/${auth.user.id}/edit`">My Profile</inertia-link>
                  <inertia-link class="block px-6 py-2 hover:bg-indigo-500 hover:text-white" href="/users">Manage Users</inertia-link>
                  <inertia-link class="block px-6 py-2 hover:bg-indigo-500 hover:text-white w-full text-left" href="/logoff" method="delete" as="button">Logout</inertia-link>
                </div>
              </template>
            </dropdown>
          </div>
        </div>
        <div class="md:flex md:grow md:overflow-hidden">
          <main-menu class="hidden md:block bg-indigo-800 shrink-0 w-56 p-12 overflow-y-auto" />
          <div class="md:flex-1 px-4 py-8 md:p-12 md:overflow-y-auto" scroll-region>
            <flash-messages />
            <slot />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>