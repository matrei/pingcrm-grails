package pingcrm

import pingcrm.auth.Role
import pingcrm.auth.UserRole
import geb.spock.GebSpec
import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import grails.testing.spock.OnceBefore
import pingcrm.pages.DashboardPage
import pingcrm.pages.LoginPage
import pingcrm.pages.OrganizationListPage
import pingcrm.pages.UserCreatePage
import pingcrm.pages.UserListPage
import spock.lang.Shared

@Integration
class OrganizationCrudSpec extends GebSpec {

    @Shared ownerUserDetails = [email: 'johndoe@example.com', password: 'secret']
    @Shared nonOwnerUserDetails = [firstName: 'Dolly', lastName: 'Dimple', email: 'abc@123.org', password: 'secret']

    @OnceBefore @Transactional
    void createNonOwnerUser() {
        Role nonOwnerRole = Role.findByAuthority 'ROLE_USER'
        def account = Account.get(1)
        def nonOwnerUser = new User(nonOwnerUserDetails + [account: account]).save failOnError: true
        UserRole.create nonOwnerUser, nonOwnerRole
    }

    void 'both owners and non-owners can view a list of all organizations'() {

        given: 'a logged in user'
        def loginPage = to LoginPage
        loginPage.login user.email, user.password
        at DashboardPage

        when: 'going to the organizations list page with a max query param of 101'
        def orgPage = to OrganizationListPage, max: 101

        then: 'there are 100 organizations listed'
        waitFor { 100 == orgPage.table.rowCount }

        where: 'the user details are'
        user << [ownerUserDetails, nonOwnerUserDetails]
    }


    void 'both owners and non-owners can create a new user'() {

        given: 'logged in user'
        def loginPage = to LoginPage
        loginPage.login user.email, user.password
        at DashboardPage

        when: 'creating a new user'
        def userCreatePage = to UserCreatePage
        userCreatePage.createUser(newUserFirstname, newUserLastname, newUserEmail, newUserPassword)

        then: 'the user is created and shown on the list page'
        def userListPage = to UserListPage
        userListPage.getIdForUser(newUserEmail) > 1

        where:
        user                | newUserFirstname | newUserLastname | newUserEmail  | newUserPassword
        ownerUserDetails    | 'Abc'            | 'Def'           | 'abc@def.com' | 'secret'
        nonOwnerUserDetails | 'Ghi'            | 'Jkl'           | 'ghi@jkl.com' | 'alsosecret'
    }
}
