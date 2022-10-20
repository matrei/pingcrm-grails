package pingcrm

import pingcrm.services.DomainService
import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import grails.web.databinding.DataBindingUtils
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

import javax.persistence.criteria.JoinType

@CompileStatic
class ContactService extends DomainService<Contact> {

    UserService userService
    OrganizationService organizationService

    @ReadOnly @Override
    List<Contact> list(Paginator paginator, Map filters) {

        def criteria = criteriaWith filters
        def pagination = paginator.queryParamsAnd sort: [lastName: 'asc', firstName: 'asc']
        (filters.trashed ? Contact.withDeleted { criteria.list(pagination) } : criteria.list(pagination)) as List<Contact>
    }

    @ReadOnly @Override
    int count(Map filters) {

        def criteria = criteriaWith filters
        (filters.trashed ? Contact.withDeleted { criteria.count() } : criteria.count()) as int
    }

    @ReadOnly @Override
    Contact get(Serializable id, boolean includeDeleted) {
        (includeDeleted ? Contact.withDeleted { Contact.get(id) } : Contact.get(id)) as Contact
    }

    @Transactional @Override
    Contact create(Object bindingSource) {

        Contact contact = new Contact()
        contact.setProperties bindingSource
        contact.account = userService.currentUser.account
        bindContactToOrganization contact, bindingSource
        contact.save()
        contact
    }

    @Transactional @Override
    Contact bindAndSave(GormEntity<Contact> contact, Object bindingSource) {

        DataBindingUtils.bindObjectToInstance contact, bindingSource
        bindContactToOrganization contact as Contact, bindingSource
        contact.save()
    }

    @Transactional
    private void bindContactToOrganization(Contact contact, Object bindingSource) {

        if(bindingSource['organizationId']) {
            def organizationId = bindingSource['organizationId'] as Serializable
            def organization = organizationService.get organizationId, true
            if(organization) contact.organization = organization
        } else {
            if(contact.organization) {
                contact.organization = null
            }
        }
    }

    @Override
    protected DetachedCriteria criteriaWith(Map filters) {
        new DetachedCriteria(Contact).build {
            join 'organization', JoinType.LEFT
            createAlias 'organization', 'org'
            eq 'account', userService.currentUser.account
            if(filters.search) {
                String search = lc(filters.search)
                or {
                    ilike 'firstName', "%$search%"
                    ilike 'lastName', "%$search%"
                    ilike 'email', "%$search%"
                    and {
                        isNotNull 'organization'
                        ilike 'org.name', "%$search%"
                    }
                }
            }
            if(filters.trashed == 'only') {
                eq 'deleted', true
            }
        }
    }
}
