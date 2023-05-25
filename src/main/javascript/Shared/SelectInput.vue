<script setup>

import { ref, watch } from 'vue'

const props = defineProps({
  id: { type: String, default() { return `select-input-${Math.round(Math.random() * 10000)}` } },
  label: String,
  error: String
})

const modelValue = defineModel({ type: [String, Number, Boolean] })

const input = ref(null)

</script>

<template>
  <div :class="$attrs.class">
    <label v-if="label" class="form-label" :for="id">{{ label }}:</label>
    <select :id="id" ref="input" v-model="modelValue" v-bind="{ ...$attrs, class: null }" class="form-select" :class="{ error: error }">
      <slot />
    </select>
    <div v-if="error" class="form-error">{{ error }}</div>
  </div>
</template>