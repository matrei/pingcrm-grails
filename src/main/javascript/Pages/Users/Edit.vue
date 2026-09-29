/*
* Copyright 2022-present original authors
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

import { router, useForm } from '@inertiajs/vue3'
import { formatFileSizeBinary } from '@/Use/use-files.js'
import { t } from '@/Use/use-i18n'

const props = defineProps({
  user: Object,
  maxPhotoSize: Number
})

const form = useForm({
  firstName: props.user.firstName,
  lastName: props.user.lastName,
  email: props.user.email,
  password: null,
  owner: props.user.owner,
  photo: null
})

const update = () => {
  form.post(`/users/${props.user.id}`, {
    forceFormData: true,
    onSuccess: () => form.reset('password', 'photo')
  })
}

const destroy = () => {
  if (confirm(t('ui.users.confirmDelete'))) {
    router.delete(`/users/${props.user.id}`)
  }
}

const restore = () => {
  if (confirm(t('ui.users.confirmRestore'))) {
    router.put(`/users/${props.user.id}/restore`)
  }
}

const photoErrors = computed(() => form.errors.photo ? [form.errors.photo] : [])

watch(() => form.photo, val => {
  if(val && val['size'] > props.maxPhotoSize) {
    form.errors.photo = t('ui.users.photoTooLarge', formatFileSizeBinary(props.maxPhotoSize, 0))
  } else {
    form.errors.photo = null
  }
})
</script>

<template>
  <inertia-head :title="`${form.firstName} ${form.lastName}`" />
  <div>
    <div class="mb-8 flex justify-start max-w-3xl">
      <h1 class="font-bold text-3xl">
        <inertia-link class="text-indigo-400 hover:text-indigo-600" href="/users">{{ $t('ui.users.title') }}</inertia-link>
        <span class="text-indigo-400 font-medium">&nbsp;/</span>
        {{ form.firstName }} {{ form.lastName }}
      </h1>
      <img v-if="user.photo" class="block w-8 h-8 rounded-full ml-4" :src="user.photo" />
    </div>
    <trashed-message v-if="user.deleted" class="mb-6" @restore="restore">
      {{ $t('ui.users.trashed') }}
    </trashed-message>
    <div class="bg-white rounded-md shadow overflow-hidden max-w-3xl">
      <form @submit.prevent="update">
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
        <div class="px-8 py-4 bg-slate-50 border-t border-slate-100 flex items-center">
          <button v-if="!user.deleted" class="text-red-600 hover:underline" tabindex="-1" type="button" @click="destroy">{{ $t('ui.users.delete') }}</button>
          <loading-button :loading="form.processing" class="btn-indigo ml-auto" type="submit">{{ $t('ui.users.update') }}</loading-button>
        </div>
      </form>
    </div>
  </div>
</template>
