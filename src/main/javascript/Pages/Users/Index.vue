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

import { router } from '@inertiajs/vue3'
import pickBy from 'lodash/pickBy'
import throttle from 'lodash/throttle'

const props = defineProps({
  filters: Object,
  users: Array
})

const form = reactive({
  search: props.filters.search,
  role: props.filters.role,
  trashed: props.filters.trashed,
})

watch(
  form,
  throttle(function () {
      router.get('/users', pickBy(form), { preserveState: true })
  }, 150), {deep: true}
)

const reset = () => {
  for (let key in form) {
    form[key] = null;
  }
}
</script>

<template>
  <inertia-head :title="$t('ui.users.title')" />
  <div>
    <h1 class="mb-8 font-bold text-3xl">{{ $t('ui.users.title') }}</h1>
    <div class="mb-6 flex justify-between items-center">
      <search-filter v-model="form.search" class="w-full max-w-md mr-4" @reset="reset">
        <label class="block text-slate-700">{{ $t('ui.filter.role') }}</label>
        <select v-model="form.role" class="mt-1 w-full form-select">
          <option :value="null" />
          <option value="user">{{ $t('ui.role.user') }}</option>
          <option value="owner">{{ $t('ui.role.owner') }}</option>
        </select>
        <label class="mt-4 block text-slate-700">{{ $t('ui.filter.trashed') }}</label>
        <select v-model="form.trashed" class="mt-1 w-full form-select">
          <option :value="null" />
          <option value="with">{{ $t('ui.filter.trashed.with') }}</option>
          <option value="only">{{ $t('ui.filter.trashed.only') }}</option>
        </select>
      </search-filter>
      <inertia-link class="btn-indigo" href="/users/create">
        <span class="md:hidden">{{ $t('ui.common.create') }}</span>
        <span class="hidden md:inline">{{ $t('ui.users.create') }}</span>
      </inertia-link>
    </div>
    <div class="bg-white rounded-md shadow-sm overflow-x-auto">
      <table class="w-full whitespace-nowrap">
        <thead>
          <tr class="text-left font-bold">
            <th class="px-6 pt-6 pb-4">{{ $t('ui.field.name') }}</th>
            <th class="px-6 pt-6 pb-4">{{ $t('ui.field.email') }}</th>
            <th class="px-6 pt-6 pb-4" colspan="2">{{ $t('ui.field.role') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id" class="hover:bg-slate-100 focus-within:bg-slate-100">
            <td class="border-t">
              <inertia-link class="px-6 py-4 flex items-center focus:text-indigo-500" :href="`/users/${user.id}/edit`">
                <img v-if="user.photo" class="block w-5 h-5 rounded-full mr-2 -my-2" :src="user.photo" />
                {{ user.name }}
                <icon v-if="user.deleted" name="trash" class="shrink-0 w-3 h-3 fill-slate-400 ml-2" />
              </inertia-link>
            </td>
            <td class="border-t">
              <inertia-link class="px-6 py-4 flex items-center" :href="`/users/${user.id}/edit`" tabindex="-1">
                {{ user.email }}
              </inertia-link>
            </td>
            <td class="border-t">
              <inertia-link class="px-6 py-4 flex items-center" :href="`/users/${user.id}/edit`" tabindex="-1">
                {{ $t(user.owner ? 'ui.role.owner' : 'ui.role.user') }}
              </inertia-link>
            </td>
            <td class="border-t w-px">
              <inertia-link class="px-4 flex items-center" :href="`/users/${user.id}/edit`" tabindex="-1">
                <icon name="cheveron-right" class="block w-6 h-6 fill-slate-400" />
              </inertia-link>
            </td>
          </tr>
          <tr v-if="users.length === 0">
            <td class="border-t px-6 py-4" colspan="4">{{ $t('ui.users.empty') }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>