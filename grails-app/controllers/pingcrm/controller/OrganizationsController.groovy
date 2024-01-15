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
package pingcrm.controller

import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileStatic
import jakarta.inject.Inject
import pingcrm.AppService
import pingcrm.Organization
import pingcrm.PublicData
import pingcrm.PublicDataMapper

/**
 * A CRUD controller for organizations.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
@Secured('IS_AUTHENTICATED_REMEMBERED')
class OrganizationsController extends AppController<Organization> {

    final List<String> indexProperties = ['id', 'name', 'phone', 'city', 'deleted']
    final List<String> editProperties = ['id', 'name', 'email', 'phone', 'address', 'city', 'region', 'country', 'postalCode', 'deleted']
    final List<String> filterNames = ['search', 'trashed']

    @Inject
    OrganizationsController(AppService appService, PublicDataMapper publicDataMapper) { super(Organization, appService, publicDataMapper) }

    @Override
    def edit(Long id) {

        def organization = findOrRedirect id
        if (!organization) return

        def contacts = appService.findAllContactsInOrganization organization
        def contactsPublicData = publicDataMapper.map(contacts as List<PublicData>, ['id', 'name', 'city', 'phone'])

        def organizationPublicData = publicDataMapper.map(organization, editProperties)
        organizationPublicData += [contacts: contactsPublicData]

        renderInertia editComponent, [organization: organizationPublicData]
    }
}