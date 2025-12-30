package pingcrm

import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.DefaultHttpClientConfiguration
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.multipart.MultipartBody
import io.micronaut.http.cookie.Cookie

import grails.testing.spock.OnceBefore

trait LoggedInHttpClientSpec {

    abstract void setHttpClient(HttpClient client)
    abstract void setSessionCookie(Cookie cookie)
    abstract void setCsrfCookie(Cookie cookie)

    @OnceBefore
    @SuppressWarnings('unused')
    void init() {
        def baseUrl = "http://localhost:$serverPort"
        httpClient = HttpClient.create(
                baseUrl.toURL(),
                new DefaultHttpClientConfiguration().tap {
                    // disable redirects to catch redirect responses (e.g. redirect to login page)
                    it.followRedirects = false
                }
        )

        // Get the csrf token
        def request = HttpRequest.GET('/login')
        def response = httpClient.toBlocking().exchange(request)
        assert response.status.code == 200
        csrfCookie = response.getCookie('XSRF-TOKEN').get()

        // Login
        def requestBody = MultipartBody.builder()
                .addPart('username', 'johndoe@example.com')
                .addPart('password', 'secret')
                .addPart('remember-me', '0')
                .build()
        request = HttpRequest.POST('/login/authenticate', requestBody)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header('X-XSRF-TOKEN', csrfCookie.value)
        response = httpClient.toBlocking().exchange(request)
        assert response.status.code == 302
        assert response.header('Location') == "$baseUrl/"
        sessionCookie = response.getCookie('JSESSIONID').get()

        // Get the csrf token again
        request = HttpRequest.GET('/').cookie(sessionCookie)
        response = httpClient.toBlocking().exchange(request)
        csrfCookie = response.getCookie('XSRF-TOKEN').get()
    }

}
