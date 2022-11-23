//file:noinspection GrUnnecessaryPublicModifier
package pingcrm

import pingcrm.services.DomainService
import gorm.logical.delete.LogicalDelete
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

@CompileStatic
class AppService {

    UserService userService
    ContactService contactService
    OrganizationService organizationService

    private <D extends LogicalDelete<D>> DomainService<D> getDomainService(Class domainClass) {

        switch(domainClass) {
            case User: return userService as DomainService<D>; break
            case Contact: return contactService as DomainService<D>; break
            case Organization: return organizationService as DomainService<D>; break
            default: throw new IllegalArgumentException("Class $domainClass not expected.")
        }
    }

    public <D extends LogicalDelete<?> & PublicData> List<D> list(Class<D> domainClass, Paginator paginator, Map filters) {
        getDomainService(domainClass).list(paginator, filters) as List<D>
    }

    public <D> int count(Class<D> domainClass, Map filters) {
        getDomainService(domainClass).count(filters)
    }

    public <D> D create(Class<D> domainClass, Object bindingSource) {
        getDomainService(domainClass).create(bindingSource) as D
    }

    public <D> boolean delete(Class<D> domainClass, Serializable id) {
        getDomainService(domainClass).delete(id)
    }

    public <D> boolean restore(Class<D> domainClass, Serializable id) {
        getDomainService(domainClass).restore(id)
    }

    public <D> D find(Class<D> domainClass, Serializable id) {
        getDomainService(domainClass).get(id, true) as D
    }

    public <D> D bindAndSave(Class<D> domainClass, GormEntity entity, Object bindingSource) {
        getDomainService(domainClass).bindAndSave(entity, bindingSource) as D
    }

    List<Contact> findAllContactsInOrganization(Organization organization) {
        organizationService.findAllContactsIn organization
    }

    User getCurrentUser() { userService.currentUser }
}
