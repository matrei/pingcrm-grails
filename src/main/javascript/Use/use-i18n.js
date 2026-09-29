/*
* Copyright 2022-present original authors
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

import { usePage } from '@inertiajs/vue3'

/*
 * Minimal i18n support.
 *
 * The locale and the UI messages (codes starting with 'ui.' in grails-app/i18n/messages*.properties)
 * are shared by the server as page props, so server side rendering and the client render the same text.
 */

function messages() {
  return usePage().props.messages ?? {}
}

/**
 * The current locale as a BCP 47 language tag, e.g. 'en' or 'sv'.
 * @returns {string}
 */
export function locale() {
  return usePage().props.locale ?? 'en'
}

/**
 * Translate a message code, replacing placeholders {0}, {1}, ... with the given arguments.
 * Returns the code itself when there is no message for it.
 * @param {string} code
 * @param {...*} args
 * @returns {string}
 */
export function t(code, ...args) {
  const message = messages()[code]
  if (message === undefined) {
    return code
  }
  return message.replace(/\{(\d+)}/g, (placeholder, index) => args[index] ?? placeholder)
}

/**
 * Translate a message code depending on a count, using the plural category of the current locale
 * (e.g. 'ui.errors.count.one' and 'ui.errors.count.other'). The count is placeholder {0}.
 * @param {string} code
 * @param {number} count
 * @param {...*} args
 * @returns {string}
 */
export function tc(code, count, ...args) {
  const pluralCode = `${code}.${new Intl.PluralRules(locale()).select(count)}`
  return t(pluralCode in messages() ? pluralCode : `${code}.other`, count, ...args)
}

/**
 * The name of a country or region in the current locale.
 * @param {string} code ISO 3166 region code, e.g. 'CA'
 * @returns {string}
 */
export function regionName(code) {
  return new Intl.DisplayNames([locale()], { type: 'region' }).of(code)
}

/**
 * A relative time in the current locale, e.g. '3 days ago' or 'för 3 dagar sedan'.
 * @param {Date|string|number} date
 * @returns {string}
 */
export function relativeTime(date) {
  const seconds = Math.round((new Date(date).getTime() - Date.now()) / 1000)
  const format = new Intl.RelativeTimeFormat(locale(), { numeric: 'auto' })
  const units = [['year', 31536000], ['month', 2592000], ['week', 604800], ['day', 86400], ['hour', 3600], ['minute', 60]]
  for (const [unit, secondsInUnit] of units) {
    if (Math.abs(seconds) >= secondsInUnit) {
      return format.format(Math.round(seconds / secondsInUnit), unit)
    }
  }
  return format.format(seconds, 'second')
}

/**
 * Vue plugin making $t and $tc available in all templates.
 */
export const i18n = {
  install(app) {
    app.config.globalProperties.$t = t
    app.config.globalProperties.$tc = tc
  }
}
