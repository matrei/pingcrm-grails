package pingcrm

import grails.testing.mixin.integration.Integration
import grails.testing.spock.OnceBefore
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.DefaultHttpClientConfiguration
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.client.multipart.MultipartBody
import io.micronaut.http.cookie.Cookie
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static io.micronaut.http.HttpMethod.*

@Integration
class AllowedHttpMethodsSpec extends Specification {

    @Shared
    @AutoCleanup
    HttpClient httpClient

    @Shared
    Cookie sessionCookie

    @SuppressWarnings('unused')
    @OnceBefore
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

    @Unroll
    void "HTTP #httpMethod is allowed for #requestPath"(String requestPath, HttpMethod httpMethod, int statusCode) {

        given: 'a request with a valid HTTP method'
        def request = HttpRequest.create(httpMethod, requestPath).cookie(sessionCookie)

        when: 'accessing an url'
        def response = httpClient.toBlocking().exchange request

        then: 'the response is as expected'
        response.status.code == statusCode

        where:
        requestPath                              | httpMethod | statusCode

        '/'                                      | GET        | 200

        '/about'                                 | GET        | 200

        '/contacts'                              | GET        | 200
        '/contacts'                              | POST       | 302
        '/contacts/create'                       | GET        | 200
        '/contacts/1/edit'                       | GET        | 200
        '/contacts/1'                            | GET        | 200
        '/contacts/1'                            | PUT        | 303
        '/contacts/1'                            | DELETE     | 303
        '/contacts/1'                            | POST       | 303
        '/contacts/1/restore'                    | PUT        | 303

        '/organizations'                         | GET        | 200
        '/organizations'                         | POST       | 302
        '/organizations/create'                  | GET        | 200
        '/organizations/1/edit'                  | GET        | 200
        '/organizations/1'                       | GET        | 200
        '/organizations/1'                       | PUT        | 303
        '/organizations/1'                       | DELETE     | 303
        '/organizations/1'                       | POST       | 303
        '/organizations/1/restore'               | PUT        | 303

        '/reports'                               | GET        | 200

        '/users'                                 | GET        | 200
        '/users'                                 | POST       | 302
        '/users/create'                          | GET        | 200
        '/users/1/edit'                          | GET        | 200
        '/users/1'                               | GET        | 200
        '/users/1'                               | PUT        | 303
        '/users/1'                               | DELETE     | 303
        '/users/1'                               | POST       | 303
        '/users/1/restore'                       | PUT        | 303
    }

    @Unroll
    void "HTTP #httpMethod is not allowed for #requestPath"(String requestPath, HttpMethod httpMethod) {

        given: 'a request with a forbidden HTTP method'
        def request = HttpRequest.create(httpMethod, requestPath).cookie(sessionCookie)

        when: 'accessing an url'
        def response = httpClient.toBlocking().exchange request

        then: 'the response is that the method is not allowed'
        !response
        def ex = thrown HttpClientResponseException
        ex.status.code == 405

        where:
        requestPath                              | httpMethod

        '/'                                      | POST
        '/'                                      | PUT
        '/'                                      | DELETE
        '/'                                      | PATCH

        '/about'                                 | POST
        '/about'                                 | PUT
        '/about'                                 | DELETE
        '/about'                                 | PATCH

        '/contacts'                              | PUT
        '/contacts'                              | DELETE
        '/contacts'                              | PATCH
        '/contacts/create'                       | POST
        '/contacts/create'                       | PUT
        '/contacts/create'                       | DELETE
        '/contacts/create'                       | PATCH
        '/contacts/1/edit'                       | POST
        '/contacts/1/edit'                       | PUT
        '/contacts/1/edit'                       | DELETE
        '/contacts/1/edit'                       | PATCH
        '/contacts/1'                            | PATCH
        '/contacts/1/restore'                    | GET
        '/contacts/1/restore'                    | POST
        '/contacts/1/restore'                    | DELETE
        '/contacts/1/restore'                    | PATCH

        '/img/user-1-123.jpg?w=60&h=60&fit=crop' | POST
        '/img/user-1-123.jpg?w=60&h=60&fit=crop' | PUT
        '/img/user-1-123.jpg?w=60&h=60&fit=crop' | DELETE
        '/img/user-1-123.jpg?w=60&h=60&fit=crop' | PATCH

        '/organizations'                         | PUT
        '/organizations'                         | DELETE
        '/organizations'                         | PATCH
        '/organizations/create'                  | POST
        '/organizations/create'                  | PUT
        '/organizations/create'                  | DELETE
        '/organizations/create'                  | PATCH
        '/organizations/1/edit'                  | POST
        '/organizations/1/edit'                  | PUT
        '/organizations/1/edit'                  | DELETE
        '/organizations/1/edit'                  | PATCH
        '/organizations/1'                       | PATCH
        '/organizations/1/restore'               | GET
        '/organizations/1/restore'               | POST
        '/organizations/1/restore'               | DELETE
        '/organizations/1/restore'               | PATCH

        '/reports'                               | POST
        '/reports'                               | PUT
        '/reports'                               | DELETE
        '/reports'                               | PATCH

        '/users'                                 | PUT
        '/users'                                 | DELETE
        '/users'                                 | PATCH
        '/users/create'                          | POST
        '/users/create'                          | PUT
        '/users/create'                          | DELETE
        '/users/create'                          | PATCH
        '/users/1/edit'                          | POST
        '/users/1/edit'                          | PUT
        '/users/1/edit'                          | DELETE
        '/users/1/edit'                          | PATCH
        '/users/1'                               | PATCH
        '/users/1/restore'                       | GET
        '/users/1/restore'                       | POST
        '/users/1/restore'                       | DELETE
        '/users/1/restore'                       | PATCH
    }

    void "HTTP GET is allowed for image controller"() {

        given: 'a GET request to the image controller'
        def request = HttpRequest.create(GET, '/img/user-1-123.jpg?w=60&h=60&fit=crop').cookie(sessionCookie)

        when: 'making the request'
        def response = httpClient.toBlocking().exchange request

        then: 'the response is 404 (because the image is not found)'
        !response
        def ex = thrown HttpClientResponseException
        ex.status.code == 404
    }
}