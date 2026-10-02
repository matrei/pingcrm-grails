package pingcrm

import java.net.http.HttpClient
import java.net.http.HttpRequest

import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import grails.testing.mixin.integration.Integration
import org.apache.grails.testing.http.client.MultipartBody
import org.apache.grails.testing.http.client.TestHttpResponse

@Integration
class CsrfSpec extends Specification implements LoggedInHttpClientSpec {

    @Shared
    @AutoCleanup
    HttpClient noRedirectHttpClient

    @Shared
    String sessionId

    @Shared
    String csrfToken

    void 'a request with an invalid CSRF token is forbidden'() {

        when: 'updating a contact with an invalid token'
            var response = sendPut(['Cookie': "JSESSIONID=$sessionId", 'X-XSRF-TOKEN': 'invalid'])

        then: 'the request is rejected'
            response.statusCode() == 403
    }

    void 'a request after the session has expired redirects back with a page expired message'() {

        given: 'a user logged in with remember-me, whose session has expired'
            var rememberMeCookie = loginWithRememberMe()

        when: 'updating a contact from the edit page'
            var response = sendPut([
                    'Cookie': rememberMeCookie,
                    'X-XSRF-TOKEN': csrfToken,
                    'Referer': "$httpBaseUrl/contacts/1/edit"
            ])

        then: 'the user is sent back to the edit page with 303 See Other, so the browser follows with a GET request'
            response.statusCode() == 303
            response.headerValue('Location') == "$httpBaseUrl/contacts/1/edit"

        when: 'following the redirect in the new session'
            var page = http(['Cookie': cookie(response, 'JSESSIONID')], '/contacts/1/edit', noRedirectHttpClient)

        then: 'the page tells the user that the page expired'
            page.statusCode() == 200
            page.body().contains('"error":"The page expired, please try again."')
    }

    void 'the page expired action cannot be requested directly'() {

        expect:
            httpAsLoggedInUser('GET', '/page-expired').statusCode() == 403
    }

    private TestHttpResponse sendPut(Map<String, String> headers) {
        var request = newHttpRequestWith('/contacts/1') {
            method('PUT', HttpRequest.BodyPublishers.ofString('{}'))
            header('Content-Type', 'application/json')
            header('X-Requested-With', 'XMLHttpRequest')
            header('X-Inertia', 'true')
            headers.each { name, value -> header(name, value) }
        }
        sendHttpRequest(request, noRedirectHttpClient)
    }

    private String loginWithRememberMe() {
        var response = http('/login', noRedirectHttpClient)
        var requestBody = MultipartBody.builder()
                .addPart('username', 'johndoe@example.com')
                .addPart('password', 'secret')
                .addPart('remember-me', 'on')
                .build()
        response = httpPostMultipart(['Cookie': cookie(response, 'JSESSIONID')], '/login/authenticate', requestBody, noRedirectHttpClient)
        assert response.statusCode() == 302
        cookie(response, 'pingcrm-remember-me')
    }

    private static String cookie(TestHttpResponse response, String name) {
        var cookie = response.headers().allValues('Set-Cookie')
                .collectMany { HttpCookie.parse(it) }
                .find { it.name == name }
        assert cookie, "No '$name' cookie in response"
        "$name=$cookie.value"
    }
}
