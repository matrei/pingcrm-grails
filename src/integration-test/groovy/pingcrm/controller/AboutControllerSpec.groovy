package pingcrm.controller

import java.net.http.HttpClient

import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import grails.testing.mixin.integration.Integration

import pingcrm.LoggedInHttpClientSpec

@Integration
class AboutControllerSpec extends Specification implements LoggedInHttpClientSpec {

    @Shared
    @AutoCleanup
    HttpClient noRedirectHttpClient

    @Shared
    String sessionId

    @Shared
    String csrfToken

    void 'controller returns git information'() {

        when: 'accessing the about page'
            def response = httpAsLoggedInUser('GET', '/about')

        then: 'the response contains the git commit information'
            response.assertContains('ebed8d6')
    }
}
