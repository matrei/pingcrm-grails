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

import { createApp, h } from 'vue'
import { createInertiaApp, Link, Head } from '@inertiajs/vue3'

import '@/Styles/index.pcss'

const pageTitleSuffix = 'Grails Inertia Demo (Ping CRM)'

createInertiaApp({
  resolve: async (name) => {
    const pages = import.meta.glob('./Pages/**/*.vue')
    return (await pages[`./Pages/${name}.vue`]()).default
  },
  title: title => title ? `${title} - ${pageTitleSuffix}` : pageTitleSuffix,
  setup ({el, App, props, plugin}) {
    createApp({render: () => h(App, props)})
      .use(plugin)
      .component('inertia-link', Link)
      .component('inertia-head', Head)
      .mount(el)
  }
})