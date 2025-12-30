/*
 * Copyright 2022-present original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pingcrm

import java.time.LocalDateTime
import java.util.regex.Pattern

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import jakarta.persistence.FetchType

import grails.compiler.GrailsCompileStatic
import grails.gorm.hibernate.annotation.ManagedEntity
import grails.gorm.hibernate.mapping.MappingBuilder
import grails.logical.delete.LogicalDelete
import org.grails.datastore.mapping.config.MappingDefinition
import org.grails.orm.hibernate.cfg.Mapping
import org.grails.orm.hibernate.cfg.PropertyConfig

import pingcrm.auth.Role
import pingcrm.auth.UserRole

/**
 * A user domain class.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@ManagedEntity
@GrailsCompileStatic
@EqualsAndHashCode(includes = 'email')
@ToString(includes = 'name', includeNames = true, includePackage = false)
class User implements LogicalDelete<User>, PublicData, Serializable {

    @SuppressWarnings('unused')
    private static final long serialVersionUID = 1

    private static final Pattern PATTERN_BACKSLASH = Pattern.compile('\\\\')

    String firstName
    String lastName
    String email
    String password
    Boolean owner = false
    String photoPath
    Boolean enabled = true
    Boolean accountExpired = false
    Boolean accountLocked = false
    Boolean passwordExpired = false

    /* timestamps in UTC set by hibernate.jdbc.time_zone */
    LocalDateTime emailVerifiedAt
    @SuppressWarnings('unused')
    LocalDateTime dateCreated
    @SuppressWarnings('unused')
    LocalDateTime lastUpdated

    /** A User belongsTo an Account */
    Account account

    /** A User hasMany Roles */
    Set<Role> getRoles() {
        UserRole.findAllRolesForUser(this)
    }

    static final constraints = {
        firstName(maxLength: 25)
        lastName(maxLength: 25)
        email(maxLength: 50, unique: true)
        emailVerifiedAt(nullable: true)
        password(password: true)
        photoPath(maxLength: 100, nullable: true)
    }

    static final MappingDefinition<Mapping, PropertyConfig> mapping = MappingBuilder.orm {
        table {
            name('`user`')
        }
        property('account') {
            fetchStrategy(FetchType.EAGER)
        }
        property('password') {
            column('`password`')
        }
        sort([
            lastName: 'asc',
            firstName: 'asc'
        ])
    }

    String getName() {
        "$firstName $lastName"
    }

    @SuppressWarnings('unused')
    String getPhoto() {
        photoPath?.replaceAll(PATTERN_BACKSLASH, '/')
    }
}
