package pingcrm.controller

import spock.lang.Specification

import grails.testing.web.UrlMappingsUnitTest
import org.grails.plugins.web.taglib.ValidationTagLib

import pingcrm.AppService
import pingcrm.PublicDataMapper
import pingcrm.UrlMappings
import pingcrm.UserService
import pingcrm.config.AppInfo
import pingcrm.config.GitInfo
import pingcrm.config.UploadConfig
import pingcrm.image.ImageProcessor
import pingcrm.image.ImageService
import pingcrm.image.ScalrImageProcessor
import pingcrm.services.FileService
import pingcrm.services.PhotoUploadValidator

class UrlMappingsSpec extends Specification implements UrlMappingsUnitTest<UrlMappings> {

    def beans = {
        bean(AppService)
        bean(UserService)
        bean(AppInfo)
        bean(GitInfo)
        bean('mapper', PublicDataMapper)
        bean('imageProcessor', ImageProcessor, ScalrImageProcessor)
        bean(ImageService) { ImageProcessor imageProcessor -> }
        bean(FileService) { ImageService imageService -> }
        bean(UploadConfig)
        bean(PhotoUploadValidator) { UploadConfig uploadConfig -> }
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

    void 'test url mappings'() {

        expect: 'calls to /about go to the about controller'
            verifyUrlMapping('/about', controller: 'about', action: 'index', method: 'GET')

        when: 'calling the images controller'
            assertForwardUrlMapping('/img/users/user-1-123.jpg?w=100&h=100', controller: 'images', action: 'thumbnail') {
                path = 'users/user-1-123.jpg'
            }

        then:
            noExceptionThrown()
    }
}
