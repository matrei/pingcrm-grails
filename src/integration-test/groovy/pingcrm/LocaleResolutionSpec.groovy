package pingcrm

import java.net.http.HttpClient

import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import grails.testing.mixin.integration.Integration
import grails.testing.spock.OnceBefore
import org.apache.grails.testing.http.client.HttpClientSupport
import org.apache.grails.testing.http.client.TestHttpResponse

@Integration
class LocaleResolutionSpec extends Specification implements HttpClientSupport {

    @Shared
    @AutoCleanup
    HttpClient noRedirectHttpClient

    @OnceBefore
    @SuppressWarnings('unused')
    void init() {
        noRedirectHttpClient = newHttpClientWith {
            followRedirects(HttpClient.Redirect.NEVER)
        }
    }

    void 'the UI language is the best supported match of the Accept-Language header'() {

        when: 'requesting the login page'
            def response = http(['Accept-Language': acceptLanguage], '/login', noRedirectHttpClient)

        then: 'the page is rendered in the expected language'
            response.assertStatus(200)
            response.assertContains("<html lang=\"$lang\"")
            response.assertContains(heading)

        where:
            acceptLanguage      | lang | heading
            'sv'                | 'sv' | 'Välkommen tillbaka!'
            'sv-SE,sv;q=0.9'    | 'sv' | 'Välkommen tillbaka!'
            'de,sv;q=0.8'       | 'sv' | 'Välkommen tillbaka!'
            'en-US,en;q=0.9'    | 'en' | 'Welcome Back!'
            'de'                | 'en' | 'Welcome Back!'
    }

    void 'the lang parameter sets the UI language for the request and the session'() {

        when: 'setting the language with the lang parameter'
            def response = http(['Accept-Language': 'en'], '/login?lang=sv', noRedirectHttpClient)

        then: 'the page is rendered in that language'
            response.assertStatus(200)
            response.assertContains('<html lang="sv"')
            response.assertContains('Välkommen tillbaka!')

        when: 'making another request in the same session'
            def sessionCookie = sessionCookieOf(response)
            response = http(['Accept-Language': 'en', 'Cookie': sessionCookie], '/login', noRedirectHttpClient)

        then: 'the session language is used instead of the Accept-Language header'
            response.assertStatus(200)
            response.assertContains('<html lang="sv"')
            response.assertContains('Välkommen tillbaka!')
    }

    void 'the lang parameter is ignored for unsupported or invalid languages'() {

        when: 'requesting a language the application is not translated into'
            def response = http(['Accept-Language': 'sv'], "/login?lang=$lang", noRedirectHttpClient)

        then: 'the language from the Accept-Language header is used'
            response.assertStatus(200)
            response.assertContains('<html lang="sv"')

        where:
            lang << ['de', '%25%25', 'x_y_z_w']
    }

    private static String sessionCookieOf(TestHttpResponse response) {
        def cookie = response.headers().allValues('Set-Cookie')
                .collectMany { HttpCookie.parse(it) }
                .find { it.name == 'JSESSIONID' }
        assert cookie, 'No session cookie in response'
        "JSESSIONID=${cookie.value}"
    }
}
