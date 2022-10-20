package pingcrm

import grails.compiler.GrailsCompileStatic

import java.time.Instant

@GrailsCompileStatic
class Account {

    String name
    @SuppressWarnings('unused') Instant dateCreated
    @SuppressWarnings('unused') Instant lastUpdated

    /** An Account hasMany Organizations */
    List<Organization> getOrganizations() { Organization.findAllByAccount(this, [sort: 'name', order: 'asc']) }

    /** An Account hasMany Contacts */
    List<Contact> getContacts() { Contact.findAllByAccount(this) }

    static constraints = {
        name maxSize: 50
    }
}