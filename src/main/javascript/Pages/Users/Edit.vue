<script>
import layout from '@/Shared/Layout.vue'
export default { layout }
</script>

<script setup>

import {computed, watch} from 'vue'
import { router, useForm } from '@inertiajs/vue3'
import TextInput from '@/Shared/TextInput.vue'
import FileInput from '@/Shared/FileInput.vue'
import SelectInput from '@/Shared/SelectInput.vue'
import LoadingButton from '@/Shared/LoadingButton.vue'
import TrashedMessage from '@/Shared/TrashedMessage.vue'
import { formatFileSizeBinary } from '@/Use/use-files.js'

const props = defineProps({
  user: Object,
  maxPhotoSize: Number,
  testServerMaxUploadSizeValidation: Boolean
})

const form = useForm({
  firstName: props.user.firstName,
  lastName: props.user.lastName,
  email: props.user.email,
  password: null,
  owner: props.user.owner,
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

const update = () => {
  form.post(`/users/${props.user.id}`, {
    forceFormData: true,
    onSuccess: () => form.reset('password', 'photo')
  })
}

const destroy = () => {
  if (confirm('Are you sure you want to delete this user?')) {
    router.delete(`/users/${props.user.id}`)
  }
}

const restore = () => {
  if (confirm('Are you sure you want to restore this user?')) {
    router.put(`/users/${props.user.id}/restore`)
  }
}

const photoErrors = computed(() => form.errors.photo ? [form.errors.photo] : [])

watch(() => form.photo, val => {
  if(val && val['size'] > props.maxPhotoSize) {
    form.errors.photo = `Photo file to large, max ${formatFileSizeBinary(props.maxPhotoSize, 0)}.`
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
        <inertia-link class="text-indigo-400 hover:text-indigo-600" href="/users">Users</inertia-link>
        <span class="text-indigo-400 font-medium">&nbsp;/</span>
        {{ form.firstName }} {{ form.lastName }}
      </h1>
      <img v-if="user.photo" class="block w-8 h-8 rounded-full ml-4" :src="user.photo" />
    </div>
    <trashed-message v-if="user.deleted" class="mb-6" @restore="restore">
      This user has been deleted.
    </trashed-message>
    <div class="bg-white rounded-md shadow overflow-hidden max-w-3xl">
      <form @submit.prevent="update">
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
        <div class="px-8 py-4 bg-slate-50 border-t border-slate-100 flex items-center">
          <button v-if="!user.deleted" class="text-red-600 hover:underline" tabindex="-1" type="button" @click="destroy">Delete User</button>
          <loading-button :loading="form.processing" class="btn-indigo ml-auto" type="submit">Update User</loading-button>
        </div>
      </form>
    </div>
  </div>
</template>