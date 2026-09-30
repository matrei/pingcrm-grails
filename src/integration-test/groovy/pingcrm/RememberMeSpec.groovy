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
class RememberMeSpec extends Specification implements HttpClientSupport {

    private static final String REMEMBER_ME_COOKIE = 'pingcrm-remember-me'

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

    void 'logging in with remember me sets an http only, same-site remember-me cookie'() {

        when: 'logging in with remember me'
            def response = loginWithRememberMe()

        then: 'the login is successful'
            response.statusCode() == 302
            response.headerValue('Location') == "$httpBaseUrl/"

        and: 'a remember-me cookie with the expected attributes is set'
            def cookie = setCookieHeader(response, REMEMBER_ME_COOKIE)
            cookie.contains('HttpOnly')
            cookie.contains('SameSite=Lax')
            cookie.contains('Max-Age=1209600')
    }

    void 'the remember-me cookie logs the user in without a session'() {

        given: 'a remember-me cookie from logging in'
            def rememberMeCookie = cookieOf(loginWithRememberMe(), REMEMBER_ME_COOKIE)

        when: 'requesting a secured page with only the remember-me cookie'
            def response = http(['Cookie': rememberMeCookie], '/', noRedirectHttpClient)

        then: 'the page is shown'
            response.statusCode() == 200
    }

    void 'logging off removes the remember-me cookie'() {

        given: 'a session and a remember-me cookie from logging in'
            def loginResponse = loginWithRememberMe()
            def cookies = "${cookieOf(loginResponse, 'JSESSIONID')}; ${cookieOf(loginResponse, REMEMBER_ME_COOKIE)}"

        when: 'logging off'
            def response = http(['Cookie': cookies], '/logoff', noRedirectHttpClient)

        then: 'the remember-me cookie is removed (emptied and expired)'
            def cookie = HttpCookie.parse(setCookieHeader(response, REMEMBER_ME_COOKIE)).first()
            cookie.value == ''
            cookie.hasExpired()
    }

    private TestHttpResponse loginWithRememberMe() {
        httpPost([:], '/login/authenticate', 'username=johndoe@example.com&password=secret&remember-me=1',
                'application/x-www-form-urlencoded', noRedirectHttpClient)
    }

    private static String setCookieHeader(TestHttpResponse response, String name) {
        def header = response.headers().allValues('Set-Cookie').find { it.startsWith("$name=") }
        assert header, "No '$name' cookie set"
        header
    }

    private static String cookieOf(TestHttpResponse response, String name) {
        setCookieHeader(response, name).takeWhile { it != ';' as char }
    }
}
