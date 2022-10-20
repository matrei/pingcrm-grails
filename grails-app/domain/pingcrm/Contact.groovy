package pingcrm

import gorm.logical.delete.LogicalDelete
import grails.compiler.GrailsCompileStatic

import java.time.Instant

@GrailsCompileStatic
class Contact implements LogicalDelete<Contact>, PublicData {

    String firstName
    String lastName
    String email
    String phone
    String address
    String city
    String region
    String country
    String postalCode
    @SuppressWarnings('unused') Instant dateCreated
    @SuppressWarnings('unused') Instant lastUpdated

    /** A Contact belongsTo an Account */
    Account account

    /** A Contact belongsTo an Organization */
    Organization organization

    static constraints = {
        firstName maxSize: 25
        lastName maxSize: 25
        email maxSize: 50, nullable: true, email: true
        phone maxSize: 50, nullable: true
        address maxSize: 150, nullable: true
        city maxSize: 50, nullable: true
        region maxSize: 50, nullable: true
        country maxSize: 2, nullable: true
        postalCode maxSize: 25, nullable: true
        organization nullable: true
    }

    static mapping = { sort lastName: 'asc', firstName: 'asc' }

    String getName() { "$firstName $lastName" }
}
