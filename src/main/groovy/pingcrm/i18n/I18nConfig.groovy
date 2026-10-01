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

import org.springframework.context.annotation.Configuration

import grails.compiler.beans.GrailsBeans
import grails.core.GrailsApplication

/**
 * The i18n configuration of the application.
 *
 * @author Mattias Reichel
 * @since 5.0.0
 */
@GrailsBeans
@CompileStatic
@Configuration(proxyBeanMethods = false)
class I18nConfig {

    static beans = {

        // Resolve the locale from the Accept-Language header (or ?lang= for the session),
        // but only to the locales the UI is translated into
        bean('localeResolver', SupportedLocaleResolver) { GrailsApplication grailsApplication ->
            var uiLocales = grailsApplication.config.getProperty('pingcrm.i18n.locales', List, ['en']).collect {
                Locale.forLanguageTag(it as String)
            }
            new SupportedLocaleResolver(uiLocales, uiLocales.first())
        }
    }
}
