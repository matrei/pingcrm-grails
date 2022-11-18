package pingcrm


import gorm.logical.delete.LogicalDelete
import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import pingcrm.auth.Role
import pingcrm.auth.UserRole

import java.time.Instant

@GrailsCompileStatic
@EqualsAndHashCode(includes='email')
@ToString(includes='name', includeNames=true, includePackage=false)
class User implements LogicalDelete<User>, PublicData, Serializable {

    @SuppressWarnings('unused')
    private static final long serialVersionUID = 1

    String firstName
    String lastName
    String email
    Instant emailVerifiedAt
    String password
    Boolean owner = false
    String photoPath
    Boolean enabled = true
    Boolean accountExpired = false
    Boolean accountLocked = false
    Boolean passwordExpired = false
    @SuppressWarnings('unused') Instant dateCreated
    @SuppressWarnings('unused') Instant lastUpdated

    /** A User belongsTo an Account */
    Account account

    /** A User hasMany Roles */
    Set<Role> getRoles() { UserRole.findAllRolesForUser(this) }

    static constraints = {
        firstName maxLength: 25
        lastName maxLength: 25
        email maxLength: 50, unique: true
        emailVerifiedAt nullable: true
        password password: true
        photoPath maxLength: 100, nullable: true
    }

    static mapping = {
        account lazy: false
	    password column: '`password`'
        sort lastName: 'asc', firstName: 'asc'
    }

    String getName() { "$firstName $lastName" }
    String getPhoto() { photoPath?.replaceAll '\\\\', '/' }
    boolean isOwner() { owner.booleanValue() }
}
