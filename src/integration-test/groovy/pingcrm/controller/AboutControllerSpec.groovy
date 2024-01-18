package pingcrm.controller

import grails.testing.mixin.integration.Integration
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.cookie.Cookie
import pingcrm.LoggedInHttpClientSpec
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

@Integration
class AboutControllerSpec extends Specification implements LoggedInHttpClientSpec {

    @Shared
    @AutoCleanup
    HttpClient httpClient

    @Shared
    Cookie sessionCookie

    void "controller returns git information"() {

        given: 'a request with a valid HTTP method'
        def request = HttpRequest.create(HttpMethod.GET, '/about').cookie(sessionCookie)

        when: 'accessing an url'
        def response = httpClient.toBlocking().exchange(request, String)

        then: 'the response contains the git commit information'
        response.body.get().contains('ebed8d6')
    }
}
