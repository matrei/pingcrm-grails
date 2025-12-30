package pingcrm

import io.micronaut.http.client.DefaultHttpClientConfiguration
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import spock.lang.Shared
import spock.lang.Specification

import grails.testing.mixin.integration.Integration
import grails.testing.spock.OnceBefore

@Integration
class AssetSecuritySpec extends Specification {

    @Shared
    HttpClient httpClient

    @SuppressWarnings('unused')
    @OnceBefore
    void init() {
        def baseUrl = "http://localhost:$serverPort"
        // disable redirects to catch redirect responses (e.g. redirect to login page)
        def config = new DefaultHttpClientConfiguration()
        config.followRedirects = false
        this.httpClient = HttpClient.create(baseUrl.toURL(), config)
    }

    void 'no login is required to access assets'() {

        when: 'accessing an asset'
            def response = httpClient.toBlocking().exchange(assetPath)

        then: 'the response is as expected'
            response.status.code == statusCode

        where:
            assetPath             | statusCode
            '/static/favicon.svg' | 200
    }

    void 'no login is required to access favicon.ico'() {

        when: 'accessing (non-existing) favicon.ico'
            httpClient.toBlocking().exchange('/favicon.ico')

        then: 'the response status is 404 Not Found'
            def ex = thrown(HttpClientResponseException)
            ex.status.code == 404
    }
}
