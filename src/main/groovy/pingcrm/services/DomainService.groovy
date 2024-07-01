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
package pingcrm.services

import pingcrm.Paginator
import gorm.logical.delete.LogicalDelete
import grails.gorm.DetachedCriteria
import grails.gorm.transactions.Transactional
import grails.web.databinding.DataBindingUtils
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

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
        return entity.save() as D
    }

    boolean delete(Serializable id) { return delete(get(id, false) as D) }
    boolean restore(Serializable id) { return unDelete(get(id, true) as D) }

    @Transactional @SuppressWarnings('GrMethodMayBeStatic')
    protected boolean delete(D entity) {
        if (!entity) return false
        entity.delete(flush: true)
        return entity.deleted
    }

    @Transactional @SuppressWarnings('GrMethodMayBeStatic')
    private boolean unDelete(D entity) {
        if (!entity) return false
        entity.unDelete()
        return !entity.deleted
    }

    protected static String lc(Object str) {
        return str?.toString()?.toLowerCase(Locale.ENGLISH)
    }
}
