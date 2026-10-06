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

import { createSSRApp, h } from 'vue'
import { createInertiaApp, Link, Head } from '@inertiajs/vue3'
import createServer from '@inertiajs/vue3/server'
import { renderToString } from '@vue/server-renderer'

import { i18n } from '@/Use/use-i18n'

const pageTitleSuffix = 'Grails Inertia Demo (Ping CRM)'

createServer(page =>
    createInertiaApp({
        page,
        render: renderToString,
        pages: './Pages',
        title: title => title ? `${title} - ${pageTitleSuffix}` : pageTitleSuffix,
        setup({ App, props, plugin }) {
            return createSSRApp({
                render: () => h(App, props)
            })
            .use(plugin)
            .use(i18n)
            .component('inertia-link', Link)
            .component('inertia-head', Head)
        }
    })
)