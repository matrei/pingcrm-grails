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
//file:noinspection GrUnnecessaryPublicModifier
package pingcrm

import pingcrm.services.DomainService
import gorm.logical.delete.LogicalDelete
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

/**
 * The main application service.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class AppService {

    UserService userService
    ContactService contactService
    OrganizationService organizationService

    private <D extends LogicalDelete<D>> DomainService<D> getDomainService(Class domainClass) {

        switch(domainClass) {
            case User: return userService as DomainService<D>; break
            case Contact: return contactService as DomainService<D>; break
            case Organization: return organizationService as DomainService<D>; break
            default: throw new IllegalArgumentException("Class $domainClass not expected.")
        }
    }

    public <D extends LogicalDelete<?> & PublicData> List<D> list(Class<D> domainClass, Paginator paginator, Map filters) {
        getDomainService(domainClass).list(paginator, filters) as List<D>
    }

    public <D> int count(Class<D> domainClass, Map filters) {
        getDomainService(domainClass).count(filters)
    }

    public <D> D create(Class<D> domainClass, Object bindingSource) {
        getDomainService(domainClass).create(bindingSource) as D
    }

    public <D> boolean delete(Class<D> domainClass, Serializable id) {
        getDomainService(domainClass).delete(id)
    }

    public <D> boolean restore(Class<D> domainClass, Serializable id) {
        getDomainService(domainClass).restore(id)
    }

    public <D> D find(Class<D> domainClass, Serializable id) {
        getDomainService(domainClass).get(id, true) as D
    }

    public <D> D bindAndSave(Class<D> domainClass, GormEntity entity, Object bindingSource) {
        getDomainService(domainClass).bindAndSave(entity, bindingSource) as D
    }

    List<Contact> findAllContactsInOrganization(Organization organization) {
        organizationService.findAllContactsIn(organization)
    }

    User getCurrentUser() {
        userService.currentUser
    }
}
