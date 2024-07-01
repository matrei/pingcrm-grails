/*
 * Copyright 2022-2024 original authors
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

import java.time.LocalDateTime

/**
 * A contact domain class.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
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

    /* timestamps in UTC set by hibernate.jdbc.time_zone */
    @SuppressWarnings('unused') LocalDateTime lastUpdated
    @SuppressWarnings('unused') LocalDateTime dateCreated

    /** A Contact belongsTo an Account */
    Account account

    /** A Contact belongsTo an Organization */
    Organization organization

    static final constraints = {
        firstName(maxSize: 25)
        lastName(maxSize: 25)
        email(maxSize: 50, nullable: true, email: true)
        phone(maxSize: 50, nullable: true)
        address(maxSize: 150, nullable: true)
        city(maxSize: 50, nullable: true)
        region(maxSize: 50, nullable: true)
        country(maxSize: 2, nullable: true)
        postalCode(maxSize: 25, nullable: true)
        organization(nullable: true)
    }

    static final mapping = { sort(lastName: 'asc', firstName: 'asc') }

    String getName() {
        "$firstName $lastName"
    }
}
