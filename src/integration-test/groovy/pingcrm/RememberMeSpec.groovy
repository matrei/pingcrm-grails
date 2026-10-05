package pingcrm

import java.net.http.HttpClient
import java.net.http.HttpRequest

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
            var response = loginWithRememberMe()

        then: 'the login is successful'
            response.assertHeaders(302,
                'Location': "$httpBaseUrl/"
            )

        and: 'a remember-me cookie with the expected attributes is set'
            var cookie = setCookieHeader(response, REMEMBER_ME_COOKIE)
            cookie.contains('HttpOnly')
            cookie.contains('SameSite=Lax')
            cookie.contains('Max-Age=1209600')
    }

    void 'the remember-me cookie logs the user in without a session'() {

        given: 'a remember-me cookie from logging in'
            var rememberMeCookie = cookieOf(loginWithRememberMe(), REMEMBER_ME_COOKIE)

        when: 'requesting a secured page with only the remember-me cookie'
            var response = http(['Cookie': rememberMeCookie], '/', noRedirectHttpClient)

        then: 'the page is shown'
            response.assertStatus(200)
    }

    void 'logging off removes the remember-me cookie'() {

        given: 'a session and a remember-me cookie from logging in'
            var loginResponse = loginWithRememberMe()
            var cookies = "${cookieOf(loginResponse, 'JSESSIONID')}; ${cookieOf(loginResponse, REMEMBER_ME_COOKIE)}"

        when: 'logging off'
            var csrfToken = csrfToken(cookies)
            var response = sendHttpRequest(newHttpRequestWith('/logoff') {
                method('DELETE', HttpRequest.BodyPublishers.noBody())
                header('Cookie', "$cookies; XSRF-TOKEN=$csrfToken")
                header('X-XSRF-TOKEN', csrfToken)
            }, noRedirectHttpClient)

        then: 'the remember-me cookie is removed (emptied and expired)'
            var cookie = HttpCookie.parse(setCookieHeader(response, REMEMBER_ME_COOKIE)).first()
            cookie.value == ''
            cookie.hasExpired()
    }

    private TestHttpResponse loginWithRememberMe() {
        var csrfToken = csrfToken()
        httpPost(['Cookie': "XSRF-TOKEN=$csrfToken", 'X-XSRF-TOKEN': csrfToken], '/login/authenticate',
                'username=johndoe@example.com&password=secret&remember-me=1',
                'application/x-www-form-urlencoded', noRedirectHttpClient)
    }

    /**
     * Gets a new CSRF token from the XSRF-TOKEN cookie set on the start page (or the login page).
     */
    private String csrfToken(String cookies = null) {
        var response = http(cookies ? ['Cookie': cookies] : [:], cookies ? '/' : '/login', noRedirectHttpClient)
        cookieOf(response, 'XSRF-TOKEN').substring('XSRF-TOKEN='.length())
    }

    private static String setCookieHeader(TestHttpResponse response, String name) {
        var header = response.headers().allValues('Set-Cookie').find { it.startsWith("$name=") }
        assert header, "No '$name' cookie set"
        header
    }

    private static String cookieOf(TestHttpResponse response, String name) {
        setCookieHeader(response, name).takeWhile { it != ';' as char }
    }
}
