package pingcrm.controller

import java.net.http.HttpClient

import groovy.json.JsonSlurper

import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import grails.testing.mixin.integration.Integration
import org.apache.grails.testing.http.client.MultipartBody

import pingcrm.LoggedInHttpClientSpec

@Integration
class UsersControllerSpec extends Specification implements LoggedInHttpClientSpec {

    private static final long USER_ID = 6

    @Shared
    @AutoCleanup
    HttpClient noRedirectHttpClient

    @Shared
    String sessionId

    @Shared
    String csrfToken

    void 'a user is updated when the photo field is sent without a file'() {

        given: 'the update of a user from a form where no photo was chosen'
            var user = editPageProps().user as Map
            var requestBody = MultipartBody.builder()
                    .addPart('firstName', user.firstName as String)
                    .addPart('lastName', user.lastName as String)
                    .addPart('email', user.email as String)
                    .addPart('owner', user.owner ? '1' : '0')
                    .addPart('photo', '', 'application/octet-stream', new byte[0])
                    .build()

        when: 'sending it'
            var response = httpPostMultipart(csrfHeaders(csrfToken, "JSESSIONID=$sessionId"), "/users/$USER_ID", requestBody, noRedirectHttpClient)

        then: 'the user is updated, without a photo'
            response.statusCode() == 303
            var props = editPageProps()
            (props.flash as Map).success == 'User updated.'
            !(props.user as Map).photo
    }

    private Map editPageProps() {
        var page = httpAsLoggedInUser('GET', "/users/$USER_ID/edit").body()
        var pageJson = (page =~ /(?s)<script data-page="[^"]*" type="application\/json">(.*?)<\/script>/)[0][1]
        (new JsonSlurper().parseText(pageJson as String) as Map).props as Map
    }
}
