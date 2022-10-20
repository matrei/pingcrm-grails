package pingcrm

import gorm.logical.delete.LogicalDelete
import grails.compiler.GrailsCompileStatic

import java.time.Instant

@GrailsCompileStatic
class Organization implements LogicalDelete<Organization>, PublicData {

    String name
    String email
    String phone
    String address
    String city
    String region
    String country
    String postalCode
    @SuppressWarnings('unused') Instant dateCreated
    @SuppressWarnings('unused') Instant lastUpdated

    /** An Organization belongsTo an Account */
    Account account

    /** An Organization hasMany Contacts */
    List<Contact> getContacts() { Contact.findAllByOrganization(this, [sort: [firstName: 'asc', lastName: 'asc']]) }

    static constraints = {
        name maxSize: 100
        email maxSize: 50, nullable: true, email: true
        phone maxSize: 50, nullable: true
        address maxSize: 150, nullable: true
        city maxSize: 50, nullable: true
        region maxSize: 50, nullable: true
        country minSize: 2, maxSize: 2, nullable: true
        postalCode maxSize: 25, nullable: true
    }
}
