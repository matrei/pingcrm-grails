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
package pingcrm.services

import groovy.transform.CompileStatic

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.Transactional
import grails.logical.delete.LogicalDelete
import grails.web.databinding.DataBindingUtils
import org.grails.datastore.gorm.GormEntity

import pingcrm.Paginator

/**
 * A base service for CRUD.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
abstract class DomainService<D extends LogicalDelete> {

    /**
     * Returns the domain object with the id (including or excluding soft deleted)
     */
    abstract D get(Serializable id, boolean includeDeleted)

    /**
     * Returns a list of domain objects (including soft deleted)
     */
    abstract List<D> list(Paginator paginator, Map filters)
    abstract int count(Map filters)
    abstract create(Object bindingSource)
    protected abstract DetachedCriteria criteriaWith(Map filters)

    @Transactional
    D bindAndSave(GormEntity<D> entity, Object bindingSource) {
        DataBindingUtils.bindObjectToInstance(entity, bindingSource)
        entity.save() as D
    }

    boolean delete(Serializable id) {
        delete(
                get(id, false) as D
        )
    }

    boolean restore(Serializable id) {
        undelete(
                get(id, true) as D
        )
    }

    @Transactional @SuppressWarnings('GrMethodMayBeStatic')
    protected boolean delete(D entity) {
        if (!entity) return false
        entity.delete(flush: true)
        entity.deleted
    }

    @Transactional @SuppressWarnings('GrMethodMayBeStatic')
    private boolean undelete(D entity) {
        if (!entity) return false
        entity.undelete()
        !entity.deleted
    }

    protected static String lc(Object str) {
        str?.toString()?.toLowerCase(Locale.ENGLISH)
    }
}
