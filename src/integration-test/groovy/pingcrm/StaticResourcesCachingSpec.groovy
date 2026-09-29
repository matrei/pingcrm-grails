package pingcrm

import spock.lang.Specification

import grails.testing.mixin.integration.Integration
import org.apache.grails.testing.http.client.HttpClientSupport

@Integration
class StaticResourcesCachingSpec extends Specification implements HttpClientSupport {

    void 'bundled javascript and css are cached for one year'() {

        when: 'requesting a bundled resource'
            def response = http("/static/dist/js/$resourceFile")

        then: 'the cache header is correct'
            response.assertHeaders(200,
                'Cache-Control': 'max-age=31536000, public'
            )

        where:
            resourceFile << [
                'test.js',
                'test.css'
            ]
    }

    void 'favicon is cached for one hour'() {

        when: 'requesting the favicon'
            def response = http('/static/favicon.svg')

        then: 'the cache header is correct'
            response.assertHeaders(200,
                'Cache-Control': 'max-age=3600, public'
            )
    }

}
