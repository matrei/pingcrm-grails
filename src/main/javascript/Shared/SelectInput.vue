<script setup>

import { ref, watch } from 'vue'

const emit = defineEmits(['update:modelValue'])

const props = defineProps({
  id: { type: String, default() { return `select-input-${Math.round(Math.random() * 10000)}` } },
  modelValue: [String, Number, Boolean],
  label: String,
  error: String
})

const input = ref(null)

const selected = ref(props.modelValue)

watch(selected, (selected) => emit('update:modelValue', selected))

</script>

<template>
  <div :class="$attrs.class">
    <label v-if="label" class="form-label" :for="id">{{ label }}:</label>
    <select :id="id" ref="input" v-model="selected" v-bind="{ ...$attrs, class: null }" class="form-select" :class="{ error: error }">
      <slot />
    </select>
    <div v-if="error" class="form-error">{{ error }}</div>
  </div>
</template>