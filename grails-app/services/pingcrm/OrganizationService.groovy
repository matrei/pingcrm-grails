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

import groovy.transform.CompileStatic

import jakarta.inject.Inject

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

import pingcrm.services.DomainService

/**
 * A service for CRUD operations on {@link pingcrm.Organization} domain objects.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class OrganizationService extends DomainService<Organization> {

    UserService userService

    @ReadOnly @Override
    List<Organization> list(Paginator paginator, Map filters) {
        def criteria = criteriaWith(filters)
        def pagination = paginator.queryParamsAnd(sort: 'name')
        (filters.trashed ? Organization.withDeleted { criteria.list(pagination) } : criteria.list(pagination)) as List<Organization>
    }

    @ReadOnly @Override
    int count(Map filters) {
        def criteria = criteriaWith(filters)
        (filters.trashed ? Organization.withDeleted { criteria.count() } : criteria.count()) as int
    }


    @ReadOnly @Override
    Organization get(Serializable id, boolean includeDeleted) {
        (includeDeleted ? Organization.withDeleted { Organization.get(id) } : Organization.get(id)) as Organization
    }

    @Transactional @Override
    Organization create(Object bindingSource) {
        Organization organization = new Organization()
        organization.setProperties(bindingSource)
        organization.account = userService.currentUser.account
        organization.save()
        return organization
    }

    @ReadOnly
    List<Contact> findAllContactsIn(Organization organization) {
        organization.contacts
    }

    @Override
    protected DetachedCriteria criteriaWith(Map filters) {
        new DetachedCriteria(Organization).build {
            eq('account', userService.currentUser.account)
            if (filters.search) {
                ilike('name', "%${lc(filters.search)}%")
            }
            if (filters.trashed == 'only') {
                eq('deleted', true)
            }
        }
    }
}
