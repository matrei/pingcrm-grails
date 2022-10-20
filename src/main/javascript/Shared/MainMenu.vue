<script setup>

import { usePage } from '@inertiajs/inertia-vue3'
import Icon from '@/Shared/Icon.vue'

const page = usePage()

const isUrl = (...urls) => {
  let currentUrl = page.url.value.substr(1)
  if (urls[0] === '') {
    return currentUrl === ''
  }
  return urls.filter(url => currentUrl.startsWith(url)).length
}

</script>

<template>
  <div>
    <div class="flex flex-col justify-between h-full">

      <div>
        <div class="mb-4">
          <inertia-link class="flex items-center group py-3" href="/">
            <icon name="dashboard" class="w-4 h-4 mr-2" :class="$page.component === 'Dashboard/Index' ? 'fill-white' : 'fill-indigo-400 group-hover:fill-white'" />
            <div :class="$page.component === 'Dashboard/Index' ? 'text-white' : 'text-indigo-300 group-hover:text-white'">Dashboard</div>
          </inertia-link>
        </div>
        <div class="mb-4">
          <inertia-link class="flex items-center group py-3" href="/organizations">
            <icon name="office" class="w-4 h-4 mr-2" :class="isUrl('organizations') ? 'fill-white' : 'fill-indigo-400 group-hover:fill-white'" />
            <div :class="isUrl('organizations') ? 'text-white' : 'text-indigo-300 group-hover:text-white'">Organizations</div>
          </inertia-link>
        </div>
        <div class="mb-4">
          <inertia-link class="flex items-center group py-3" href="/contacts">
            <icon name="users" class="w-4 h-4 mr-2" :class="isUrl('contacts') ? 'fill-white' : 'fill-indigo-400 group-hover:fill-white'" />
            <div :class="isUrl('contacts') ? 'text-white' : 'text-indigo-300 group-hover:text-white'">Contacts</div>
          </inertia-link>
        </div>
        <div class="mb-4">
          <inertia-link class="flex items-center group py-3" href="/reports">
            <icon name="printer" class="w-4 h-4 mr-2" :class="isUrl('reports') ? 'fill-white' : 'fill-indigo-400 group-hover:fill-white'" />
            <div :class="isUrl('reports') ? 'text-white' : 'text-indigo-300 group-hover:text-white'">Reports</div>
          </inertia-link>
        </div>
      </div>
      <div>
        <div class="mb-4">
          <inertia-link class="flex items-center group py-3" href="/users">
            <icon name="book" class="w-4 h-4 mr-2" :class="isUrl('users') ? 'fill-white' : 'fill-indigo-400 group-hover:fill-white'" />
            <div :class="isUrl('users') ? 'text-white' : 'text-indigo-300 group-hover:text-white'">Users</div>
          </inertia-link>
        </div>
        <div>
          <inertia-link class="flex items-center group py-3" href="/about">
            <icon name="apple" class="w-4 h-4 mr-2" :class="isUrl('about') ? 'fill-white' : 'fill-indigo-400 group-hover:fill-white'" />
            <div :class="isUrl('about') ? 'text-white' : 'text-indigo-300 group-hover:text-white'">About</div>
          </inertia-link>
        </div>
      </div>
    </div>
  </div>
</template>