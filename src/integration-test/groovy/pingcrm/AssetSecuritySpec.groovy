package pingcrm

import java.net.http.HttpClient

import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import grails.testing.mixin.integration.Integration
import grails.testing.spock.OnceBefore
import org.apache.grails.testing.http.client.HttpClientSupport

@Integration
class AssetSecuritySpec extends Specification implements HttpClientSupport {

    @Shared
    @AutoCleanup
    HttpClient noRedirectHttpClient

    @OnceBefore
    @SuppressWarnings('unused')
    void init() {
        // disable redirects to catch redirect responses (e.g. redirect to login page)
        noRedirectHttpClient = newHttpClientWith {
            followRedirects(HttpClient.Redirect.NEVER)
        }
    }

    void 'no login is required to access assets'() {

        when: 'accessing an asset'
            def response = http(assetPath, noRedirectHttpClient)

        then: 'the response is as expected'
            response.assertStatus(statusCode)

        where:
            assetPath             | statusCode
            '/static/favicon.svg' | 200
    }

    void 'no login is required to access favicon.ico'() {

        when: 'accessing (non-existing) favicon.ico'
            def response = http('/favicon.ico', noRedirectHttpClient)

        then: 'the response status is 404 Not Found'
            response.assertStatus(404)
    }
}
