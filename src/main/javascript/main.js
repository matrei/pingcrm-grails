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