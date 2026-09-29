package pingcrm.i18n

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

import spock.lang.Specification

class SupportedLocaleResolverSpec extends Specification {

    private static final Locale SWEDISH = Locale.forLanguageTag('sv')

    private final SupportedLocaleResolver resolver = new SupportedLocaleResolver([Locale.ENGLISH, SWEDISH], Locale.ENGLISH)

    void 'the best supported match of the Accept-Language header is used'() {

        given: 'a request with an Accept-Language header'
            def request = new MockHttpServletRequest()
            if (acceptLanguage) request.addHeader('Accept-Language', acceptLanguage)

        expect: 'the expected locale is resolved'
            resolver.resolveLocale(request) == expectedLocale
            resolver.resolveLocaleContext(request).locale == expectedLocale

        where:
            acceptLanguage   | expectedLocale
            null             | Locale.ENGLISH
            'sv'             | SWEDISH
            'sv-SE'          | SWEDISH
            'de, sv;q=0.8'   | SWEDISH
            'en-US'          | Locale.ENGLISH
            'de'             | Locale.ENGLISH
    }

    void 'a supported locale set for the session overrides the Accept-Language header'() {

        given: 'a request preferring English'
            def request = new MockHttpServletRequest()
            request.addHeader('Accept-Language', 'en')

        when: 'setting a locale for the session'
            resolver.setLocale(request, new MockHttpServletResponse(), locale)

        then: 'the matching supported locale is used'
            resolver.resolveLocale(request) == expectedLocale

        where:
            locale                        | expectedLocale
            SWEDISH                       | SWEDISH
            Locale.forLanguageTag('sv-FI') | SWEDISH
            Locale.GERMAN                 | Locale.ENGLISH
    }

    void 'the lang query parameter is applied when the locale is resolved'() {

        given: 'a request with a lang parameter in the query string'
            def request = new MockHttpServletRequest()
            request.addHeader('Accept-Language', 'en')
            request.queryString = "lang=$lang"

        and: 'a following request in the same session without the parameter'
            def nextRequest = new MockHttpServletRequest()
            nextRequest.addHeader('Accept-Language', 'en')

        when: 'resolving the locale of both requests'
            def locale = resolver.resolveLocaleContext(request).locale
            nextRequest.session = request.session
            def nextLocale = resolver.resolveLocale(nextRequest)

        then: 'the locale is resolved accordingly and kept for the session'
            locale == expectedLocale
            nextLocale == expectedLocale

        where:
            lang     | expectedLocale
            'sv'     | SWEDISH
            'sv_SE'  | SWEDISH
            'de'     | Locale.ENGLISH
            '%25%25' | Locale.ENGLISH
    }
}
