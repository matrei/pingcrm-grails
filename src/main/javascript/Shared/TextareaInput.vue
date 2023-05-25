<script setup>
import { ref } from 'vue'

defineProps({
  id: { type: String, default() { return `textarea-input-${Math.round(Math.random() * 10000)}` } },
  label: String,
  error: String
})

const modelValue = defineModel({ type: String })

const input = ref(null)

</script>

<template>
  <div :class="$attrs.class">
    <label v-if="label" class="form-label" :for="id">{{ label }}:</label>
    <textarea :id="id" ref="input" v-bind="{ ...$attrs, class: null }" class="form-textarea" :class="{ error: error }" v-model="modelValue" />
    <div v-if="error" class="form-error">{{ error }}</div>
  </div>
</template>