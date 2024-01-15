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

<script setup>

defineProps({
  id: { type: String, default() { return `text-input-${Math.round(Math.random() * 10000)}` } },
  type: { type: String, default: 'text' },
  label: String,
  error: String
})

const modelValue = defineModel({ type: String })

const input = ref(null)

</script>

<template>
  <div :class="$attrs.class">
    <label v-if="label" class="form-label" :for="id">{{ label }}:</label>
    <input :id="id" ref="input" v-bind="{ ...$attrs, class: null }" class="form-input" :class="{ error: error }" :type="type" v-model="modelValue" />
    <div v-if="error" class="form-error">{{ error }}</div>
  </div>
</template>