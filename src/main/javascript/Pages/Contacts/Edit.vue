<script>
import layout from '@/Shared/Layout.vue'
export default { layout }
</script>

<script setup>

import { router, useForm } from '@inertiajs/vue3'
import TextInput from '@/Shared/TextInput.vue'
import SelectInput from '@/Shared/SelectInput.vue'
import LoadingButton from '@/Shared/LoadingButton.vue'
import TrashedMessage from '@/Shared/TrashedMessage.vue'

const props = defineProps({
  contact: Object,
  organizations: Array,
})

const form = useForm({
  firstName: props.contact.firstName,
  lastName: props.contact.lastName,
  organizationId: props.contact.organizationId,
  email: props.contact.email,
  phone: props.contact.phone,
  address: props.contact.address,
  city: props.contact.city,
  region: props.contact.region,
  country: props.contact.country,
  postalCode: props.contact.postalCode,
})

const update = () => form.put(`/contacts/${props.contact.id}`)

const destroy = () => {
  if (confirm('Are you sure you want to delete this contact?')) {
    router.delete(`/contacts/${props.contact.id}`)
  }
}

const restore = () => {
  if (confirm('Are you sure you want to restore this contact?')) {
    router.put(`/contacts/${props.contact.id}/restore`)
  }
}

</script>

<template>
  <inertia-head :title="`${form.firstName} ${form.lastName}`" />
  <div>
    <h1 class="mb-8 font-bold text-3xl">
      <inertia-link class="text-indigo-400 hover:text-indigo-600" href="/contacts">Contacts</inertia-link>
      <span class="text-indigo-400 font-medium">&nbsp;/</span>
      {{ form.firstName }} {{ form.lastName }}
    </h1>
    <trashed-message v-if="contact.deleted" class="mb-6" @restore="restore">
      This contact has been deleted.
    </trashed-message>
    <div class="bg-white rounded-md shadow overflow-hidden max-w-3xl">
      <form @submit.prevent="update">
        <div class="p-8 -mr-6 -mb-8 flex flex-wrap">
          <text-input v-model="form.firstName" :error="form.errors.firstName" class="pr-6 pb-8 w-full lg:w-1/2" label="First name" />
          <text-input v-model="form.lastName" :error="form.errors.lastName" class="pr-6 pb-8 w-full lg:w-1/2" label="Last name" />
          <select-input v-model="form.organizationId" :error="form.errors.organizationId" class="pr-6 pb-8 w-full lg:w-1/2" label="Organization">
            <option :value="null" />
            <option v-for="organization in organizations" :key="organization.id" :value="organization.id">{{ organization.name }}</option>
          </select-input>
          <text-input v-model="form.email" :error="form.errors.email" class="pr-6 pb-8 w-full lg:w-1/2" label="Email" />
          <text-input v-model="form.phone" :error="form.errors.phone" class="pr-6 pb-8 w-full lg:w-1/2" label="Phone" />
          <text-input v-model="form.address" :error="form.errors.address" class="pr-6 pb-8 w-full lg:w-1/2" label="Address" />
          <text-input v-model="form.city" :error="form.errors.city" class="pr-6 pb-8 w-full lg:w-1/2" label="City" />
          <text-input v-model="form.region" :error="form.errors.region" class="pr-6 pb-8 w-full lg:w-1/2" label="Province/State" />
          <select-input v-model="form.country" :error="form.errors.country" class="pr-6 pb-8 w-full lg:w-1/2" label="Country">
            <option :value="null" />
            <option value="CA">Canada</option>
            <option value="US">United States</option>
          </select-input>
          <text-input v-model="form.postalCode" :error="form.errors.postalCode" class="pr-6 pb-8 w-full lg:w-1/2" label="Postal code" />
        </div>
        <div class="px-8 py-4 bg-slate-50 border-t border-slate-100 flex items-center">
          <button v-if="!contact.deleted" class="text-red-600 hover:underline" tabindex="-1" type="button" @click="destroy">Delete Contact</button>
          <loading-button :loading="form.processing" class="btn-indigo ml-auto" type="submit">Update Contact</loading-button>
        </div>
      </form>
    </div>
  </div>
</template>