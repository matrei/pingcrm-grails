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

import grails.compiler.GrailsCompileStatic
import grails.gorm.hibernate.annotation.ManagedEntity

/**
 * An account domain class.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@ManagedEntity
@GrailsCompileStatic
class Account {

    String name

    /** timestamps in UTC set by hibernate.jdbc.time_zone */
    @SuppressWarnings('unused')
    LocalDateTime dateCreated
    @SuppressWarnings('unused')
    LocalDateTime lastUpdated

    /** An Account hasMany Organizations */
    List<Organization> getOrganizations() {
        Organization.createCriteria().list {
            eq('account', this)
            order('name', 'asc')
        } as List<Organization>
    }

    /** An Account hasMany Contacts */
    List<Contact> getContacts() {
        Contact.createCriteria().list {
            eq('account', this)
        } as List<Contact>
    }

    static final constraints = {
        name(maxSize: 50)
    }
}