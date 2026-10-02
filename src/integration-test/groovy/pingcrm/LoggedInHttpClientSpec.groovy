package pingcrm

import java.net.http.HttpClient
import java.net.http.HttpRequest

import grails.testing.spock.OnceBefore
import org.apache.grails.testing.http.client.HttpClientSupport
import org.apache.grails.testing.http.client.MultipartBody
import org.apache.grails.testing.http.client.TestHttpResponse

trait LoggedInHttpClientSpec implements HttpClientSupport {

    abstract void setNoRedirectHttpClient(HttpClient client)
    abstract void setSessionId(String sessionId)
    abstract void setCsrfToken(String csrfToken)

    @OnceBefore
    @SuppressWarnings('unused')
    void init() {
        // disable redirects to catch redirect responses (e.g. redirect to login page)
        noRedirectHttpClient = newHttpClientWith {
            followRedirects(HttpClient.Redirect.NEVER)
        }

        // Get the csrf token
        csrfToken = newCsrfToken()

        // Login
        var requestBody = MultipartBody.builder()
                .addPart('username', 'johndoe@example.com')
                .addPart('password', 'secret')
                .addPart('remember-me', '0')
                .build()
        var response = httpPostMultipart(csrfHeaders(csrfToken), '/login/authenticate', requestBody, noRedirectHttpClient)
        assert response.statusCode() == 302
        assert response.headerValue('Location') == "$httpBaseUrl/"
        sessionId = cookieValue(response, 'JSESSIONID')

        // Get the csrf token again, as logging in replaces it
        response = http(['Cookie': "JSESSIONID=$sessionId"], '/', noRedirectHttpClient)
        csrfToken = cookieValue(response, 'XSRF-TOKEN')
    }

    /**
     * Sends a body-less request as the logged-in user, without following redirects.
     */
    TestHttpResponse httpAsLoggedInUser(String httpMethod, String path) {
        var request = newHttpRequestWith(path) {
            method(httpMethod, HttpRequest.BodyPublishers.noBody())
            header('Cookie', "JSESSIONID=$sessionId; XSRF-TOKEN=$csrfToken")
            header('X-XSRF-TOKEN', csrfToken)
        }
        sendHttpRequest(request, noRedirectHttpClient)
    }

    /**
     * Gets a new CSRF token from the XSRF-TOKEN cookie set on the login page.
     */
    String newCsrfToken() {
        var response = http('/login', noRedirectHttpClient)
        assert response.statusCode() == 200
        cookieValue(response, 'XSRF-TOKEN')
    }

    /**
     * The headers that send a CSRF token, both in the XSRF-TOKEN cookie and in the X-XSRF-TOKEN header.
     */
    static Map<String, String> csrfHeaders(String csrfToken, String cookies = null) {
        ['Cookie': cookies ? "$cookies; XSRF-TOKEN=$csrfToken" : "XSRF-TOKEN=$csrfToken", 'X-XSRF-TOKEN': csrfToken]
    }

    static String cookieValue(TestHttpResponse response, String name) {
        var cookie = response.headers().allValues('Set-Cookie')
                .collectMany { HttpCookie.parse(it) }
                .find { it.name == name }
        assert cookie, "No '$name' cookie in response"
        cookie.value
    }
}
