/*
 * Copyright 2022-2023 original authors
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

import grails.compiler.GrailsCompileStatic

import java.time.LocalDateTime

/**
 * An account domain object.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@GrailsCompileStatic
class Account {

    String name

    /** timestamps in UTC set by hibernate.jdbc.time_zone */
    @SuppressWarnings('unused') LocalDateTime dateCreated
    @SuppressWarnings('unused') LocalDateTime lastUpdated

    /** An Account hasMany Organizations */
    List<Organization> getOrganizations() { Organization.findAllByAccount this, [sort: 'name', order: 'asc'] }

    /** An Account hasMany Contacts */
    List<Contact> getContacts() { Contact.findAllByAccount this }

    static constraints = {
        name maxSize: 50
    }
}