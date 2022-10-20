package pingcrm.controller


import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileStatic
import jakarta.inject.Inject
import pingcrm.AppService
import pingcrm.Organization

@CompileStatic
@Secured('IS_AUTHENTICATED_REMEMBERED')
class OrganizationsController extends AppController<Organization> {

    final List<String> indexProperties = ['id', 'name', 'phone', 'city', 'deleted']
    final List<String> editProperties = ['id', 'name', 'email', 'phone', 'address', 'city', 'region', 'country', 'postalCode', 'deleted']
    final List<String> filterNames = ['search', 'trashed']

    @Inject
    OrganizationsController(AppService appService) { super(Organization, appService) }

    @Override
    def edit(Long id) {

        def organization = findOrRedirect id
        if(!organization) return

        def contacts = appService.findAllContactsInOrganization organization
        def contactsPublicData = contacts*.publicData 'id', 'name', 'city', 'phone'

        def organizationPublicData = organization.publicData editProperties
        organizationPublicData += [contacts: contactsPublicData]

        renderInertia editComponent, [organization: organizationPublicData]
    }
}