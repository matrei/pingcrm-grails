import { createSSRApp, h } from 'vue'
import { createInertiaApp, Link, Head } from '@inertiajs/vue3'
import createServer from '@inertiajs/vue3/server'
import { renderToString } from '@vue/server-renderer'

const pageTitleSuffix = 'Grails Inertia Demo (Ping CRM)'

createServer(page =>
    createInertiaApp({
        page,
        render: renderToString,
        resolve: async (name) => {
            const pages = import.meta.glob('./Pages/**/*.vue')
            return (await pages[`./Pages/${name}.vue`]()).default
        },
        title: title => title ? `${title} - ${pageTitleSuffix}` : pageTitleSuffix,
        setup({ App, props, plugin }) {
            return createSSRApp({
                render: () => h(App, props)
            })
            .use(plugin)
            .component('inertia-link', Link)
            .component('inertia-head', Head)
        }
    })
)