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

        given: 'a user'
            var user = editPageUser()
            var requestBody = MultipartBody.builder()
                    .addPart('firstName', user.firstName as String)
                    .addPart('lastName', user.lastName as String)
                    .addPart('email', user.email as String)
                    .addPart('owner', user.owner ? '1' : '0')
                    .addPart('photo', 'avatar.jpg', 'image/jpeg', new File('src/integration-test/resources/assets/avatar.jpg').bytes)
                    .build()

        when: 'uploading a photo of the user'
            var uploadResponse = httpPostMultipart(csrfHeaders(csrfToken, "JSESSIONID=$sessionId"), "/users/$USER_ID", requestBody, noRedirectHttpClient)

        then: 'the photo is saved'
            uploadResponse.assertStatus(303)

        when: 'requesting the thumbnail of the photo'
            var thumbnailUrl = editPageUser().photo as String
            var response = httpAsLoggedInUser('GET', thumbnailUrl)

        then: 'it is a JPEG image that the browser can keep, as the image at a URL never changes'
            response.assertHeaders(200,
                'Content-Type': 'image/jpeg',
                'Cache-Control': 'max-age=31536000, private, immutable'
            )

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
