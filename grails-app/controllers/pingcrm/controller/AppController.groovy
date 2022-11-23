package pingcrm.controller


import gorm.logical.delete.LogicalDelete
import grails.gorm.PagedResultList
import groovy.transform.CompileStatic
import pingcrm.AppService
import pingcrm.Paginator
import pingcrm.PublicData
import pingcrm.ValidationMessageRenderer

@CompileStatic
abstract class AppController<D extends LogicalDelete & PublicData> implements ValidationMessageRenderer {

    private static final String ATTRIBUTE_KEY_MODEL = 'pingcrm.controller.INERTIA_MODEL'

    static allowedMethods = [
        index: 'GET',
        create: 'GET',
        store: 'POST',
        edit: ['GET', 'PUT'],
        update: ['PUT','POST'],
        delete: 'DELETE',
        restore: 'PUT'
    ]

    private final Class<D> domainClass

    protected final AppService appService
    protected final String entityName
    protected final String entityNameLC
    protected final String collectionName
    protected final String indexComponent
    protected final String createComponent
    protected final String editComponent

    protected AppController(Class<D> domainClass, AppService appService) {
        this.domainClass = domainClass
        this.appService = appService
        entityName = domainClass.simpleName
        entityNameLC = entityName.toLowerCase Locale.ENGLISH
        collectionName = "${entityNameLC}s"
        indexComponent = "${entityName}s/Index"
        createComponent = "${entityName}s/Create"
        editComponent = "${entityName}s/Edit"
    }

    abstract List getIndexProperties()
    abstract List getEditProperties()
    abstract List getFilterNames()

    def index(Paginator paginator) {

        def filters = params.subMap filterNames
        def list = appService.list domainClass, paginator, filters
        def totalCount = getTotalCount list, filters
        def publicData = list*.publicData indexProperties

        renderInertia indexComponent, currentModel + [
            (collectionName): paginator.paginate(publicData, params, [total: totalCount, locale: request.locale]),
            filters: filters
        ]
    }

    def create() { renderInertia createComponent, currentModel }

    def edit(Long id) {

        D entity = findOrRedirect(id)
        if(!entity) return null

        renderInertia editComponent, currentModel + [
            (entityNameLC): (entity as PublicData).publicData(editProperties)
        ]
    }

    def store() {

        D entity = appService.create domainClass, request.JSON
        if(!entity) {
            flash.error = "Failed to create $entityNameLC"
            redirect action: 'create'; return
        }

        if(entity.hasErrors()) { chain action: 'create', model: [errors: renderErrors(entity.errors)]; return }

        flash.success = "$entityName created."
        redirect action: 'index'
    }

    def update(Long id) {

        D entity = findOrRedirect id
        if(!entity) return

        if(!appService.bindAndSave(domainClass, entity, request.JSON)) {
            chain action: 'edit', id: id, model: [errors: renderErrors(entity.errors)]; return
        }

        flash.success = "$entityName updated."
        redirect action: 'edit', id: id
    }

    def delete(Long id) {

        def deleted = appService.delete domainClass, id
        if(deleted) flash.success = "$entityName deleted."
        else flash.error = "Failed to delete $entityNameLC."

        redirect action: 'edit', id: id
    }

    def restore(Long id) {

        def restored = appService.restore domainClass, id
        if(restored) flash.success = "$entityName restored."
        else flash.error = "Failed to restore $entityNameLC."

        redirect action: 'edit', id: id
    }

    private int getTotalCount(List list, Map filters) {

        if(list instanceof PagedResultList) {
            return (list as PagedResultList).totalCount
        }
        appService.count domainClass, filters
    }

    protected D findOrRedirect(Serializable id) {

        D entity = errors.hasErrors() ? null : appService.find(domainClass, id)
        if(!entity) {
            flash.error = "$entityName not found."
            redirect action: 'index'; return null
        }

        entity
    }

    protected final void addToModel(Map model) {

        def modelMap = currentModel
        modelMap.putAll model
        request.setAttribute ATTRIBUTE_KEY_MODEL, modelMap
    }

    protected final Map getCurrentModel() { request.getAttribute(ATTRIBUTE_KEY_MODEL) as Map ?: [:] }
}