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
package pingcrm.controller

import groovy.transform.CompileStatic

import jakarta.inject.Inject

import pingcrm.AppService
import pingcrm.Contact
import pingcrm.PublicData
import pingcrm.PublicDataMapper

/**
 * A CRUD controller for contacts.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class ContactsController extends AppController<Contact> {

    final List<String> indexProperties = ['id', 'name', 'organization.name', 'phone', 'city', 'deleted']
    final List<String> editProperties = ['id', 'firstName', 'lastName', 'organizationId', 'email', 'phone', 'address', 'city', 'region', 'country', 'postalCode', 'deleted']
    final List<String> organizationProperties = ['id', 'name']
    final List<String> filterNames = ['search', 'trashed']

    @Inject
    ContactsController(AppService appService, PublicDataMapper publicDataMapper) {
        super(Contact, appService, publicDataMapper)
    }

    @Override
    def create() {
        var organizations = appService.currentUser.account.organizations
        var organizationsPublicData = publicDataMapper.map(organizations as List<PublicData>, organizationProperties)
        renderInertia(createComponent, [organizations: organizationsPublicData])
    }

    @Override
    def edit(Long id) {
        var contact = findOrRedirect(id)
        if (!contact) return

        var contactPublicData = publicDataMapper.map(contact, editProperties)
        var organizations = appService.currentUser.account.organizations
        var organizationsPublicData = publicDataMapper.map(organizations as List<PublicData>, organizationProperties)

        renderInertia(editComponent, [
            contact: contactPublicData,
            organizations: organizationsPublicData
        ])
    }
}
