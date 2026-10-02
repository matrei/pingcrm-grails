package pingcrm

import java.net.http.HttpClient

import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import grails.testing.mixin.integration.Integration
import org.apache.grails.testing.http.client.TestHttpResponse

@Integration
class SecuritySpec extends Specification implements LoggedInHttpClientSpec {

    @Shared
    @AutoCleanup
    HttpClient noRedirectHttpClient

    @Shared
    String sessionId

    @Shared
    String csrfToken

    void 'logging off with HTTP #httpMethod ends the session'() {

        given: 'a logged in user'
            init()

        when: 'logging off'
            var response = httpAsLoggedInUser(httpMethod, '/logoff')

        then: 'the user is redirected to the start page with 303 See Other, so the browser follows with a GET request'
            response.statusCode() == 303
            redirectPath(response) == '/'

        and: 'the session is no longer logged in'
            redirectPath(httpAsLoggedInUser('GET', '/')) == '/login'

        where:
            httpMethod << ['POST', 'DELETE']
    }

    void 'logging off with HTTP GET is not possible'() {

        given: 'a logged in user'
            init()

        when: 'trying to log off with GET, which is not protected against CSRF'
            var response = httpAsLoggedInUser('GET', '/logoff')

        then: 'the request is forbidden'
            response.statusCode() == 403

        and: 'the session is still logged in'
            httpAsLoggedInUser('GET', '/').statusCode() == 200
    }

    void 'anonymous #description requests to secured pages are redirected to the login page'() {

        when: 'requesting a secured page without being logged in'
            var response = http(headers, '/contacts', noRedirectHttpClient)

        then: 'the response is a redirect to the login page'
            response.statusCode() == 303
            redirectPath(response) == '/login'

        where:
            description | headers
            'page'      | [:]
            'Inertia'   | ['X-Requested-With': 'XMLHttpRequest', 'X-Inertia': 'true']
    }

    void 'logging in with invalid credentials redirects to the authentication failure page'() {

        when: 'logging in with invalid credentials'
            var response = httpPost(csrfHeaders(newCsrfToken()), '/login/authenticate', 'username=nobody&password=wrong',
                    'application/x-www-form-urlencoded', noRedirectHttpClient)

        then: 'the user is redirected to the authentication failure page'
            response.statusCode() == 302
            response.headerValue('Location') == "$httpBaseUrl/login/authfail?login_error=1"
    }

    void 'unknown URLs are forbidden for logged in users'() {

        given: 'a logged in user'
            init()

        expect: 'unknown URLs to be forbidden'
            httpAsLoggedInUser('GET', '/hello').statusCode() == 403
    }

    void 'unknown URLs redirect anonymous users to the login page'() {

        when: 'requesting an unknown URL without being logged in'
            var response = http('/hello', noRedirectHttpClient)

        then: 'the response is a redirect to the login page'
            response.statusCode() == 303
            redirectPath(response) == '/login'
    }

    private static String redirectPath(TestHttpResponse response) {
        URI.create(response.headerValue('Location')).path
    }
}
