package pingcrm

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.DefaultHttpClientConfiguration
import io.micronaut.http.client.HttpClient
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import grails.testing.mixin.integration.Integration
import grails.testing.spock.OnceBefore

@Integration
class StaticResourcesCachingSpec extends Specification {

    @Shared
    @AutoCleanup
    HttpClient httpClient

    @OnceBefore
    @SuppressWarnings('unused')
    void init() {
        def baseUrl = "http://localhost:$serverPort"
        httpClient = HttpClient.create(
                baseUrl.toURL(),
                new DefaultHttpClientConfiguration()
        )
    }

    void 'bundled javascript and css are cached for one year'() {
        given: 'a request for a bundled resource'
            def request = HttpRequest.GET("/static/dist/js/$resourceFile")

        when: 'requesting the resource'
            def response = httpClient.toBlocking().exchange(request)

        then: 'the cache header is correct'
            response.status.code == 200
            response.header('Cache-Control') == 'max-age=31536000, public'

        where:
            resourceFile << [
                'test.js',
                'test.css'
            ]
    }

    void 'favicon is cached for one hour'() {

        given: 'a request for the favicon'
            def request = HttpRequest.GET('/static/favicon.svg')

        when: 'requesting the favicon'
            def response = httpClient.toBlocking().exchange(request)

        then: 'the cache header is correct'
            response.status.code == 200
            response.header('Cache-Control') == 'max-age=3600, public'
    }

}
