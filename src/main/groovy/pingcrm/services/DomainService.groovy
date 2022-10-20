package pingcrm.services

import pingcrm.Paginator
import gorm.logical.delete.LogicalDelete
import grails.gorm.DetachedCriteria
import grails.gorm.transactions.Transactional
import grails.web.databinding.DataBindingUtils
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

@CompileStatic
abstract class DomainService<D extends LogicalDelete> {

    /**
     * Returns the domain object with the id (exluding soft deleted)
     */
    abstract D get(Serializable id, boolean includeDeleted)
    /**
     * Returns the domain object with the id (including soft deleted)
     */
    abstract List<D> list(Paginator paginator, Map filters)
    abstract int count(Map filters)
    abstract create(Object bindingSource)
    protected abstract DetachedCriteria criteriaWith(Map filters)

    @Transactional
    D bindAndSave(GormEntity<D> entity, Object bindingSource) {
        DataBindingUtils.bindObjectToInstance entity, bindingSource
        entity.save() as D
    }

    boolean delete(Serializable id) { delete(get(id, false) as D) }
    boolean restore(Serializable id) { unDelete(get(id, true) as D) }

    @Transactional @SuppressWarnings('GrMethodMayBeStatic')
    protected boolean delete(D entity) {
        if(!entity) return false
        entity.delete(flush: true)
        entity.deleted
    }

    @Transactional @SuppressWarnings('GrMethodMayBeStatic')
    private boolean unDelete(D entity) {
        if(!entity) return false
        entity.unDelete()
        !entity.deleted
    }

    protected static String lc(Object str) { str?.toString()?.toLowerCase(Locale.ENGLISH) }
}
