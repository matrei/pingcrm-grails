package pingcrm

import java.net.http.HttpClient
import java.net.http.HttpRequest

import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import grails.testing.mixin.integration.Integration
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

    void 'the CSRF token is set in a cookie that JavaScript can read'() {

        when: 'requesting the login page'
            var response = http('/login', noRedirectHttpClient)

        then: 'the XSRF-TOKEN cookie is set without HttpOnly, so Inertia can send it back in a header'
            var cookie = response.headers().allValues('Set-Cookie').find { it.startsWith('XSRF-TOKEN=') }
            cookie
            !cookie.contains('HttpOnly')
    }

    void 'a request with the CSRF token is accepted'() {

        expect:
            sendPut(csrfHeaders(csrfToken, "JSESSIONID=$sessionId")).statusCode() == 303
    }

    void 'a request without a CSRF token is forbidden'() {

        expect:
            sendPut(['Cookie': "JSESSIONID=$sessionId; XSRF-TOKEN=$csrfToken"]).statusCode() == 403
    }

    void 'a request with a CSRF token that does not match the cookie is forbidden'() {

        expect:
            sendPut(['Cookie': "JSESSIONID=$sessionId; XSRF-TOKEN=$csrfToken", 'X-XSRF-TOKEN': 'invalid']).statusCode() == 403
    }

    void 'logging in without a CSRF token is forbidden'() {

        when: 'logging in without a CSRF token'
            var response = httpPost([:], '/login/authenticate', 'username=johndoe@example.com&password=secret',
                    'application/x-www-form-urlencoded', noRedirectHttpClient)

        then: 'the login is rejected'
            response.statusCode() == 403
    }

    private TestHttpResponse sendPut(Map<String, String> headers) {
        var request = newHttpRequestWith('/contacts/1') {
            // An empty update, which changes nothing
            method('PUT', HttpRequest.BodyPublishers.ofString('{}'))
            header('Content-Type', 'application/json')
            header('X-Requested-With', 'XMLHttpRequest')
            header('X-Inertia', 'true')
            headers.each { name, value -> header(name, value) }
        }
        sendHttpRequest(request, noRedirectHttpClient)
    }
}
