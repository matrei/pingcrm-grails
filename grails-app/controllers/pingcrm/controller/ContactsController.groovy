package pingcrm.controller


import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileStatic
import jakarta.inject.Inject
import pingcrm.AppService
import pingcrm.Contact

@CompileStatic
@Secured('IS_AUTHENTICATED_REMEMBERED')
class ContactsController extends AppController<Contact> {

    final List<String> indexProperties = ['id', 'name', 'organization.name', 'phone', 'city', 'deleted']
    final List<String> editProperties = ['id', 'firstName', 'lastName', 'organizationId', 'email', 'phone', 'address', 'city', 'region', 'country', 'postalCode', 'deleted']
    final List<String> organizationProperties = ['id', 'name']
    final List<String> filterNames = ['search', 'trashed']

    @Inject
    ContactsController(AppService appService) { super(Contact, appService) }

    @Override
    def create() {

        def organizations = appService.currentUser.account.organizations
        def organizationsPublicData = organizations*.publicData organizationProperties

        renderInertia createComponent, [ organizations: organizationsPublicData ]
    }

    @Override
    def edit(Long id) {

        def contact = findOrRedirect id
        if(!contact) return

        def contactPublicData = contact.publicData editProperties

        def organizations = appService.currentUser.account.organizations
        def organizationsPublicData = organizations*.publicData organizationProperties

        renderInertia editComponent, [
            contact: contactPublicData,
            organizations: organizationsPublicData
        ]
    }
}
