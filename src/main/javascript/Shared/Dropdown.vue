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

import { autoUpdate, computePosition, flip, shift } from '@floating-ui/dom'

const props = defineProps({
  placement: { type: String, default: 'bottom-end' },
  autoClose: { type: Boolean, default: true },
})

const root = ref(null)
const dropdown = ref(null)
let show = ref(false)
let stopAutoUpdate

const updatePosition = () => {
  computePosition(root.value, dropdown.value, {
    placement: props.placement,
    middleware: [flip(), shift()],
  }).then(({ x, y }) => {
    Object.assign(dropdown.value.style, { left: `${x}px`, top: `${y}px` })
  })
}

const stopPositioning = () => {
  stopAutoUpdate?.()
  stopAutoUpdate = null
}

watch(show, (isVisible) => {
  if (isVisible) {
    nextTick(() => {
      // Keeps the dropdown positioned while the page is scrolled or resized
      stopAutoUpdate = autoUpdate(root.value, dropdown.value, updatePosition)
    })
  } else {
    stopPositioning()
  }
})

onUnmounted(stopPositioning)

onMounted(() => {
  document.addEventListener('keydown', e => {
    if (e.keyCode === 27) {
      show.value = false
    }
  })
})

</script>

<template>
  <button type="button" @click="show = true" ref="root">
    <slot />
    <teleport to="body">
      <div v-if="show">
        <div style="position: fixed; top: 0; right: 0; left: 0; bottom: 0; z-index: 99998; background: black; opacity: .2" @click="show = false" />
        <div ref="dropdown" style="position: absolute; z-index: 99999;" @click.stop="show = !autoClose">
          <slot name="dropdown" />
        </div>
      </div>
    </teleport>
  </button>
</template>