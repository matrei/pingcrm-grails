package pingcrm.controller

import grails.testing.web.UrlMappingsUnitTest
import org.grails.plugins.web.taglib.ValidationTagLib
import pingcrm.AppService
import pingcrm.FileService
import pingcrm.UrlMappings
import pingcrm.UserService
import spock.lang.Specification

class UrlMappingsSpec extends Specification implements UrlMappingsUnitTest<UrlMappings> {

    Closure doWithSpring() {
        { ->
            appService(AppService)
            fileService(FileService)
            userService(UserService)
        }
    }

    void setup() {
        mockTagLib(ValidationTagLib)
        mockController(AboutController)
        mockController(ContactsController)
        mockController(DashboardController)
        mockController(ImagesController)
        mockController(LoginController)
        mockController(OrganizationsController)
        mockController(ReportsController)
        mockController(UsersController)
    }

    void "test url mappings"() {

        expect: "calls to /about go to the about controller"
        verifyUrlMapping('/about', controller: 'about', action: 'index', method: 'GET')
    }
}
