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

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import org.springframework.context.i18n.LocaleContext
import org.springframework.context.i18n.SimpleLocaleContext
import org.springframework.util.StringUtils
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.springframework.web.util.UriComponentsBuilder

/**
 * A session based locale resolver that only resolves to locales the application is translated into.
 *
 * Until a locale is explicitly set for the session (with the {@code ?lang=} request parameter),
 * the best match from the browser's {@code Accept-Language} header is used. Locales that the
 * application has no translation for are ignored and the default locale is used instead.
 *
 * The {@code ?lang=} parameter is applied as soon as the locale of a request is resolved,
 * so the new locale is used for the whole request regardless of the order of the handler interceptors
 * (Grails' own {@code ParamsAwareLocaleChangeInterceptor} will just set the same locale again).
 *
 * @author Mattias Reichel
 * @since 5.0.0
 */
@CompileStatic
class SupportedLocaleResolver extends SessionLocaleResolver {

    static final String DEFAULT_PARAM_NAME = 'lang'

    private final List<Locale> supportedLocales

    String paramName = DEFAULT_PARAM_NAME

    SupportedLocaleResolver(List<Locale> supportedLocales, Locale defaultLocale) {
        this.supportedLocales = supportedLocales
        var acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver()
        acceptHeaderLocaleResolver.supportedLocales = supportedLocales
        acceptHeaderLocaleResolver.defaultLocale = defaultLocale
        defaultLocaleFunction = { HttpServletRequest request -> acceptHeaderLocaleResolver.resolveLocale(request) }
    }

    @Override
    Locale resolveLocale(HttpServletRequest request) {
        applyLocaleParameter(request)
        super.resolveLocale(request)
    }

    @Override
    LocaleContext resolveLocaleContext(HttpServletRequest request) {
        applyLocaleParameter(request)
        super.resolveLocaleContext(request)
    }

    @Override
    void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        if (localeContext?.locale == null) {
            super.setLocaleContext(request, response, localeContext)
            return
        }
        var supportedLocale = findSupportedLocale(localeContext.locale)
        if (supportedLocale) {
            super.setLocaleContext(request, response, new SimpleLocaleContext(supportedLocale))
        }
    }

    /**
     * Finds the supported locale matching the given locale, first by an exact match
     * and then by language only (e.g. {@code sv_SE} matches {@code sv}).
     *
     * @param locale the locale to find a match for
     * @return the matching supported locale or {@code null} if there is no match
     */
    Locale findSupportedLocale(Locale locale) {
        supportedLocales.find { it == locale } ?:
                supportedLocales.find { it.language == locale.language }
    }

    private void applyLocaleParameter(HttpServletRequest request) {
        // Only the query string is read, so a (possibly invalid) request body is never parsed here
        if (!request.queryString) {
            return
        }
        var localeParam = UriComponentsBuilder.fromUriString("?${request.queryString}").build()
                .queryParams.getFirst(paramName)
        if (!localeParam) {
            return
        }
        try {
            var locale = StringUtils.parseLocale(URLDecoder.decode(localeParam, 'UTF-8'))
            if (locale) {
                setLocaleContext(request, null, new SimpleLocaleContext(locale))
            }
        } catch (IllegalArgumentException ignore) {
            // Not a valid locale, keep the current one
        }
    }
}
