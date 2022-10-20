package pingcrm


import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import pingcrm.services.DomainService

@CompileStatic
class OrganizationService extends DomainService<Organization> {

    UserService userService

    @ReadOnly @Override
    List<Organization> list(Paginator paginator, Map filters) {
        def criteria = criteriaWith filters
        def pagination = paginator.queryParamsAnd sort: 'name'
        (filters.trashed ? Organization.withDeleted { criteria.list(pagination) } : criteria.list(pagination)) as List<Organization>
    }

    @ReadOnly @Override
    int count(Map filters) {
        def criteria = criteriaWith filters
        (filters.trashed ? Organization.withDeleted { criteria.count() } : criteria.count()) as int
    }


    @Transactional @Override
    Organization get(Serializable id, boolean includeDeleted) {
        (includeDeleted ? Organization.withDeleted { Organization.get(id) } : Organization.get(id)) as Organization
    }

    @Transactional @Override
    Organization create(Object bindingSource) {
        Organization organization = new Organization()
        organization.setProperties bindingSource
        organization.account = userService.currentUser.account
        organization.save()
        organization
    }

    @ReadOnly
    List<Contact> findAllContactsIn(Organization organization) {
        organization.contacts
    }

    @Override
    protected DetachedCriteria criteriaWith(Map filters) {
        new DetachedCriteria(Organization).build {
            eq 'account', userService.currentUser.account
            if(filters.search) {
                ilike 'name', "%${lc(filters.search)}%"
            }
            if(filters.trashed == 'only') {
                eq 'deleted', true
            }
        }
    }
}
