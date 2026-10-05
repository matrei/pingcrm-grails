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
class ImagesControllerSpec extends Specification implements LoggedInHttpClientSpec {

    private static final long USER_ID = 5

    @Shared
    @AutoCleanup
    HttpClient noRedirectHttpClient

    @Shared
    String sessionId

    @Shared
    String csrfToken

    void 'a thumbnail is a JPEG image that the browser can keep'() {

        given: 'a user with a photo'
            var user = editPageUser()
            var requestBody = MultipartBody.builder()
                    .addPart('firstName', user.firstName as String)
                    .addPart('lastName', user.lastName as String)
                    .addPart('email', user.email as String)
                    .addPart('owner', user.owner ? '1' : '0')
                    .addPart('photo', 'avatar.jpg', 'image/jpeg', new File('src/integration-test/resources/assets/avatar.jpg').bytes)
                    .build()
            var uploadResponse = httpPostMultipart(csrfHeaders(csrfToken, "JSESSIONID=$sessionId"), "/users/$USER_ID", requestBody, noRedirectHttpClient)
            assert uploadResponse.statusCode() == 303
            var thumbnailUrl = editPageUser().photo as String

        when: 'requesting the thumbnail of the photo'
            var response = httpAsLoggedInUser('GET', thumbnailUrl)

        then: 'it is a JPEG image'
            response.statusCode() == 200
            response.headerValue('Content-Type') == 'image/jpeg'

        and: 'the browser can keep it, as the image at a URL never changes'
            response.headerValue('Cache-Control') == 'max-age=31536000, private, immutable'

        cleanup: 'remove the uploaded photo'
            if (thumbnailUrl) {
                new File(System.getProperty('java.io.tmpdir'), URI.create(thumbnailUrl).path - '/img/').delete()
            }
    }

    private Map editPageUser() {
        var page = httpAsLoggedInUser('GET', "/users/$USER_ID/edit").body()
        var pageJson = (page =~ /(?s)<script data-page="[^"]*" type="application\/json">(.*?)<\/script>/)[0][1]
        (new JsonSlurper().parseText(pageJson as String) as Map).props.user as Map
    }
}
