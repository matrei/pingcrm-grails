<script setup>
import { ref } from 'vue'

defineEmits(['update:modelValue'])

defineProps({
  id: { type: String, default() { return `textarea-input-${Math.round(Math.random() * 10000)}` } },
  modelValue: String,
  label: String,
  error: String
})

const input = ref(null)

</script>

<template>
  <div :class="$attrs.class">
    <label v-if="label" class="form-label" :for="id">{{ label }}:</label>
    <textarea :id="id" ref="input" v-bind="{ ...$attrs, class: null }" class="form-textarea" :class="{ error: error }" :value="modelValue" @input="$emit('update:modelValue', $event.target.value)" />
    <div v-if="error" class="form-error">{{ error }}</div>
  </div>
</template>