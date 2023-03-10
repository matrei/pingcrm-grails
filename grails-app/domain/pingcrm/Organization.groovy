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

import gorm.logical.delete.LogicalDelete
import grails.compiler.GrailsCompileStatic

import java.time.Instant

/**
 * An organization domain object.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
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
    List<Contact> getContacts() { Contact.findAllByOrganization this, [sort: [firstName: 'asc', lastName: 'asc']] }

    static final constraints = {
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
