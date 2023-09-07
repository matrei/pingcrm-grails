<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  label: String,
  accept: String,
  errors: { type: Array, default: () => [] }
})

const modelValue = defineModel()

const file = ref(null)

watch (
  () => modelValue,
  (value) => { if (!value) file.value.value = '' }
)

const filesize = (size) => {
  const i = Math.floor(Math.log(size) / Math.log(1024))
  return (size / Math.pow(1024, i)).toFixed(2) * 1 + ' ' + ['B', 'kB', 'MB', 'GB', 'TB'][i]
}

const browse = () => file.value.click()
const change = (e) => modelValue.value = e.target.files[0]
const remove = () => modelValue.value = null

</script>

<template>
  <div :class="$attrs.class">
    <label v-if="label" class="form-label">{{ label }}:</label>
    <div class="form-input p-0" :class="{ error: errors.length }">
      <input ref="file" type="file" :accept="accept" class="hidden" @change="change" />
      <div v-if="!modelValue" class="p-2">
        <button type="button" class="px-4 py-1 bg-slate-500 hover:bg-slate-700 rounded-sm text-xs font-medium text-white" @click="browse">
          Browse
        </button>
      </div>
      <div v-else class="flex items-center justify-between p-2">
        <div class="flex-1 pr-1">
          {{ modelValue.name }} <span class="text-slate-500 text-xs">({{ filesize(modelValue.size) }})</span>
        </div>
        <button type="button" class="px-4 py-1 bg-slate-500 hover:bg-slate-700 rounded-sm text-xs font-medium text-white" @click="remove">
          Remove
        </button>
      </div>
    </div>
    <div v-if="errors.length" class="form-error">{{ errors[0] }}</div>
  </div>
</template>