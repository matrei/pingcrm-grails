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
package pingcrm.i18n

import groovy.transform.CompileStatic

import jakarta.inject.Inject

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Resolves messages from the application's message bundles (grails-app/i18n)
 * in the locale of the current request.
 *
 * @author Mattias Reichel
 * @since 5.0.0
 */
@CompileStatic
trait LocalizedMessages {

    @Inject
    MessageSource messageSource

    /**
     * Resolves a message in the locale of the current request.
     *
     * @param code the message code
     * @param args the message arguments
     * @return the resolved message, or the code if no message is found
     */
    String localizedMessage(String code, Object... args) {
        messageSource.getMessage(code, args, code, LocaleContextHolder.locale)
    }
}
