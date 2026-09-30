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

import { useForm } from '@inertiajs/vue3'
import { formatFileSizeBinary } from '@/Use/use-files.js'
import { t } from '@/Use/use-i18n'

const props = defineProps({
  maxPhotoSize: Number
})

const form = useForm({
  firstName: null,
  lastName: null,
  email: null,
  password: null,
  owner: false,
  photo: null
})

const photoErrors = computed(() => form.errors.photo ? [form.errors.photo] : [])

watch(() => form.photo, val => {
  if(val && val['size'] > props.maxPhotoSize) {
    form.errors.photo = t('ui.users.photoTooLarge', formatFileSizeBinary(props.maxPhotoSize, 0))
  } else {
    form.errors.photo = null
  }
})

const store = () => { form.post('/users', { forceFormData: true }) }

</script>

<template>
  <inertia-head :title="$t('ui.users.create')" />
  <div>
    <h1 class="mb-8 font-bold text-3xl">
      <inertia-link class="text-indigo-400 hover:text-indigo-600" href="/users">{{ $t('ui.users.title') }}</inertia-link>
      <span class="text-indigo-400 font-medium">&nbsp;/</span> {{ $t('ui.common.create') }}
    </h1>
    <div class="bg-white rounded-md shadow-sm overflow-hidden max-w-3xl">
      <form @submit.prevent="store">
        <div class="p-8 -mr-6 -mb-8 flex flex-wrap">
          <text-input v-model="form.firstName" :error="form.errors.firstName" class="pr-6 pb-8 w-full lg:w-1/2" :label="$t('ui.field.firstName')" />
          <text-input v-model="form.lastName" :error="form.errors.lastName" class="pr-6 pb-8 w-full lg:w-1/2" :label="$t('ui.field.lastName')" />
          <text-input v-model="form.email" :error="form.errors.email" class="pr-6 pb-8 w-full lg:w-1/2" :label="$t('ui.field.email')" />
          <text-input v-model="form.password" :error="form.errors.password" class="pr-6 pb-8 w-full lg:w-1/2" type="password" autocomplete="new-password" :label="$t('ui.field.password')" />
          <select-input v-model="form.owner" :error="form.errors.owner" class="pr-6 pb-8 w-full lg:w-1/2" :label="$t('ui.field.owner')">
            <option :value="true">{{ $t('ui.common.yes') }}</option>
            <option :value="false">{{ $t('ui.common.no') }}</option>
          </select-input>
          <file-input v-model="form.photo" :errors="photoErrors" class="pr-6 pb-8 w-full lg:w-1/2" type="file" accept="image/*" :label="$t('ui.field.photo')" />
        </div>
        <div class="px-8 py-4 bg-slate-50 border-t border-slate-100 flex justify-end items-center">
          <loading-button :loading="form.processing" class="btn-indigo" type="submit">{{ $t('ui.users.create') }}</loading-button>
        </div>
      </form>
    </div>
  </div>
</template>
