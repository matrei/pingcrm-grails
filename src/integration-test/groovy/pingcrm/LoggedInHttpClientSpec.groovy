package pingcrm

import grails.testing.spock.OnceBefore
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.DefaultHttpClientConfiguration
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.multipart.MultipartBody
import io.micronaut.http.cookie.Cookie

trait LoggedInHttpClientSpec {

    abstract void setHttpClient(HttpClient client)
    abstract void setSessionCookie(Cookie cookie)

    @OnceBefore
    @SuppressWarnings('unused')
    void init() {
        def baseUrl = "http://localhost:$serverPort"
        // disable redirects to catch redirect responses (e.g. redirect to login page)
        def config = new DefaultHttpClientConfiguration()
        config.followRedirects = false
        httpClient = HttpClient.create(baseUrl.toURL(), config)
        def requestBody = MultipartBody.builder()
                .addPart('username', 'johndoe@example.com')
                .addPart('password', 'secret')
                .addPart('remember-me', '0')
                .build()
        def request = HttpRequest.POST('/login/authenticate', requestBody).contentType(MediaType.MULTIPART_FORM_DATA)
        def response = httpClient.toBlocking().exchange(request)
        assert response.status.code == 302
        assert response.header('Location') == "$baseUrl/"
        sessionCookie = response.getCookie('JSESSIONID').get()
    }

}
