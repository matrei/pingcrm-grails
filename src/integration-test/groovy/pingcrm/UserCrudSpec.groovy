package pingcrm


import geb.spock.GebSpec
import grails.testing.mixin.integration.Integration
import pingcrm.pages.DashboardPage
import pingcrm.pages.LoginPage
import pingcrm.pages.UserCreatePage
import pingcrm.pages.UserEditPage
import pingcrm.pages.UserListPage

@Integration
class UserCrudSpec extends GebSpec {

    void 'the demo user can log in'() {

        when: 'the demo user tries to log in'
        loginDemoUser()

        then: 'the demo demo user is logged in'
        at DashboardPage
    }

    void 'deleting the logged in user will log the user out'() {

        given: 'a logged in demo user'
        loginDemoUser()

        and: 'some user information'
        def firstname = 'Jenna'
        def lastname = 'Johnson'
        def email= 'test@test.com'
        def password = 'secret'

        when: 'creating a user'
        def userCreatePage = to UserCreatePage
        userCreatePage.createUser firstname, lastname, email, password

        and: 'getting the id of the new user from the user list'
        def userListPage = at UserListPage
        def userId = userListPage.getIdForUser email

        and: 'logging out the demo user'
        go '/logoff'

        and: 'logging in the created user'
        def loginPage = to LoginPage
        loginPage.login email, password
        at DashboardPage

        and: 'deleting the created and logged in user'
        def userEditPage = new UserEditPage(userId)
        to userEditPage
        withConfirm { userEditPage.deleteBtn.click() }

        then: 'a redirect to the login page is performed'
        at LoginPage
    }

    void 'it is not allowed to update the demo user'() {

        given: 'a logged in demo user'
        loginDemoUser()

        when: 'going to the edit user page for the demo user'
        def userEditPage = new UserEditPage(1)
        to userEditPage

        and: 'trying to update the user'
        userEditPage.save()

        then: 'an error message is shown'
        userEditPage.flashError == 'Updating the demo user is not allowed.'
    }

    void 'it is not allowed to delete the demo user'() {

        given: 'a logged in demo user'
        loginDemoUser()

        when: 'going to the edit user page for the demo user'
        def userEditPage = new UserEditPage(1)
        to userEditPage

        and: 'trying to delete the user'
        withConfirm { userEditPage.delete() }

        then: 'an error message is shown'
        userEditPage.flashError == 'Deleting the demo user is not allowed.'
    }

    void 'it is allowed to update a user that is not the demo user'() {

        given: 'a logged in demo user'
        loginDemoUser()

        when: 'going to the edit user page for a user that is not the demo user'
        def userEditPage = new UserEditPage(2)
        to userEditPage

        and: 'trying to update the user'
        userEditPage.save()

        then: 'a success message is shown'
        userEditPage.flashSuccess == 'User updated.'
    }

    void 'it is allowed to delete a user that is not a demo user'() {

        given: 'a logged in demo user'
        def loginPage = to LoginPage
        loginPage.loginDemoUser()

        when: 'going to the edit user page for a user that is not the demo user'
        def userEditPage = new UserEditPage(2)
        to userEditPage

        and: 'trying to delete the user'
        withConfirm { userEditPage.delete() }

        then: 'a success message is shown'
        userEditPage.flashSuccess == 'User deleted.'
    }

    void 'users can be created with valid input'() {

        given: 'a logged in demo user'
        loginDemoUser()

        when: 'going to the create user page'
        def userCreatePage = new UserCreatePage()
        to userCreatePage

        and: 'submitting valid user details'
        def file = 'src/integration-test/resources/assets/avatar.jpg' as File
        userCreatePage.createUser 'Han', 'Solo', 'han@solo.com', 'secret', file

        then: 'the user is redirected to the user list page'
        def userListPage = at UserListPage

        and: 'a success message is shown'
        userListPage.flashSuccess == 'User created.'

        and: 'the avatar image is shown'
        waitFor { js.exec userListPage.firstAvatarImage, 'return arguments[0].complete' }
        js.exec(userListPage.firstAvatarImage, 'return arguments[0].naturalHeight') > 0

        cleanup: 'remove the generated avatar image'
        def avatarFile = "src/main/webapp/users/${userListPage.firstAvatarImageFilename}" as File
        avatarFile.delete()
    }

    void 'users cannot be created with a too large photo file size (server-side validation: #testServerMaxUploadSizeValidation)'(boolean testServerMaxUploadSizeValidation) {

        given: 'a logged in demo user'
        loginDemoUser()

        when: 'going to the create user page'
        def userCreatePage = new UserCreatePage()
        to userCreatePage

        and: 'submitting the form with a photo that has a too large file size'
        def file = 'src/integration-test/resources/assets/avatar-filesize-too-large.jpg' as File
        userCreatePage.createUser 'Josh', 'Doe', 'josh@doe.com', 'secret', file

        then: 'an error message is shown'
        waitFor { userCreatePage.flashError == 'There is one form error.' }
        userCreatePage.photoFieldBorder.hasClass 'error'

        where:
        testServerMaxUploadSizeValidation << [false, true]
    }

    void 'the user can upload an avatar image'()  {

        given: 'a logged in demo user'
        loginDemoUser()

        when: 'going to the edit user page for a user that is not the demo user'
        def userEditPage = new UserEditPage(2)
        to userEditPage

        and: 'trying to upload an avatar image'
        userEditPage.photoField.file = 'src/integration-test/resources/assets/avatar.jpg' as File
        userEditPage.save()

        then: 'a success message is shown'
        waitFor { userEditPage.flashSuccess == 'User updated.' }

        and: 'the avatar image is shown'
        waitFor { js.exec userEditPage.avatarImage, 'return arguments[0].complete' }
        js.exec(userEditPage.avatarImage, 'return arguments[0].naturalHeight') > 0

        cleanup: 'remove the generated avatar image'
        def avatarFile = "src/main/webapp/users/${userEditPage.avatarImageFilename}" as File
        avatarFile.delete()
    }

    void 'the user cannot upload files larger than allowed (server-side validation: #testServerMaxUploadSizeValidation)'(boolean testServerMaxUploadSizeValidation) {

        given: 'a logged in demo user'
        loginDemoUser()

        when: 'going to the edit user page for a user that is not the demo user'
        def userEditPage = new UserEditPage(3)
        userEditPage.testServerMaxUploadSizeValidation = testServerMaxUploadSizeValidation
        to userEditPage

        and: 'trying to upload an image file that is to big'
        userEditPage.photoField.file = 'src/integration-test/resources/assets/avatar-filesize-too-large.jpg' as File
        userEditPage.save()

        then: 'an error message is shown'
        waitFor { userEditPage.flashError == 'There is one form error.' }
        userEditPage.photoFieldBorder.hasClass 'error'

        where:
        testServerMaxUploadSizeValidation << [false, true]
    }

    LoginPage loginDemoUser() {
        def loginPage = to LoginPage
        loginPage.loginDemoUser()
        loginPage
    }
}
