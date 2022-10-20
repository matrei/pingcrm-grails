<script>
import layout from '@/Shared/Layout.vue'
export default { layout }
</script>

<script setup>

import { computed, watch } from 'vue'
import { useForm } from '@inertiajs/inertia-vue3'
import FileInput from '@/Shared/FileInput.vue'
import TextInput from '@/Shared/TextInput.vue'
import SelectInput from '@/Shared/SelectInput.vue'
import LoadingButton from '@/Shared/LoadingButton.vue'
import { formatFileSizeBinary } from '@/Use/use-files.js'

const props = defineProps({
  maxPhotoSize: Number,
  testServerMaxUploadSizeValidation: Boolean
})

const form = useForm({
  firstName: null,
  lastName: null,
  email: null,
  password: null,
  owner: false,
  photo: null
})

form.transform(data => {
  if(!props.testServerMaxUploadSizeValidation) {
    if(data.photo && data.photo.size > props.maxPhotoSize) {
      return { ...data, photo: null, clientSaysPhotoTooLarge: props.maxPhotoSize }
    }
  }
  return data
})

const photoErrors = computed(() => form.errors.photo ? [form.errors.photo] : [])

watch(() => form.photo, val => {
  if(val && val['size'] > props.maxPhotoSize) {
    form.errors.photo = `Photo file to large, max ${formatFileSizeBinary(props.maxPhotoSize, 0)}.`
  } else {
    form.errors.photo = null
  }
})

const store = () => { form.post('/users', { forceFormData: true }) }

</script>

<template>
  <inertia-head title="Create User" />
  <div>
    <h1 class="mb-8 font-bold text-3xl">
      <inertia-link class="text-indigo-400 hover:text-indigo-600" href="/users">Users</inertia-link>
      <span class="text-indigo-400 font-medium">&nbsp;/</span> Create
    </h1>
    <div class="bg-white rounded-md shadow overflow-hidden max-w-3xl">
      <form @submit.prevent="store">
        <div class="p-8 -mr-6 -mb-8 flex flex-wrap">
          <text-input v-model="form.firstName" :error="form.errors.firstName" class="pr-6 pb-8 w-full lg:w-1/2" label="First name" />
          <text-input v-model="form.lastName" :error="form.errors.lastName" class="pr-6 pb-8 w-full lg:w-1/2" label="Last name" />
          <text-input v-model="form.email" :error="form.errors.email" class="pr-6 pb-8 w-full lg:w-1/2" label="Email" />
          <text-input v-model="form.password" :error="form.errors.password" class="pr-6 pb-8 w-full lg:w-1/2" type="password" autocomplete="new-password" label="Password" />
          <select-input v-model="form.owner" :error="form.errors.owner" class="pr-6 pb-8 w-full lg:w-1/2" label="Owner">
            <option :value="true">Yes</option>
            <option :value="false">No</option>
          </select-input>
          <file-input v-model="form.photo" :errors="photoErrors" class="pr-6 pb-8 w-full lg:w-1/2" type="file" accept="image/*" label="Photo" />
        </div>
        <div class="px-8 py-4 bg-slate-50 border-t border-slate-100 flex justify-end items-center">
          <loading-button :loading="form.processing" class="btn-indigo" type="submit">Create User</loading-button>
        </div>
      </form>
    </div>
  </div>
</template>