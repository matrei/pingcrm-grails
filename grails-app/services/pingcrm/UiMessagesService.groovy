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
package pingcrm

import java.util.concurrent.ConcurrentHashMap

import groovy.transform.CompileStatic

import org.springframework.context.MessageSource

import grails.util.Environment

/**
 * Provides the messages used by the client side UI.
 *
 * UI messages are regular messages in grails-app/i18n/messages*.properties
 * with codes starting with {@code ui.}. They are resolved without arguments,
 * so any placeholders (e.g. {@code {0}}) are left for the client to fill in.
 *
 * @author Mattias Reichel
 * @since 5.0.0
 */
@CompileStatic
class UiMessagesService {

    static final String UI_MESSAGE_PREFIX = 'ui.'
    static final String DEFAULT_BUNDLE = 'messages.properties'

    MessageSource messageSource

    private final Map<Locale, Map<String, String>> cache = new ConcurrentHashMap<>()

    /**
     * @param locale the locale to resolve the messages for
     * @return the UI messages for the locale, keyed by message code
     */
    Map<String, String> messagesFor(Locale locale) {
        if (Environment.current == Environment.DEVELOPMENT) {
            // Do not cache in development so changes to message bundles are picked up
            return resolveMessages(locale)
        }
        return cache.computeIfAbsent(locale) { resolveMessages(it) }
    }

    private Map<String, String> resolveMessages(Locale locale) {
        uiMessageCodes.collectEntries { code ->
            [code, messageSource.getMessage(code, null, locale)]
        }
    }

    private Collection<String> getUiMessageCodes() {
        var bundle = new Properties()
        getClass().classLoader.getResourceAsStream(DEFAULT_BUNDLE)?.withCloseable {
            bundle.load(it)
        }
        bundle.stringPropertyNames().findAll { it.startsWith(UI_MESSAGE_PREFIX) }.sort()
    }
}
