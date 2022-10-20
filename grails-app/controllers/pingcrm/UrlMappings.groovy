package pingcrm

class UrlMappings {

    static mappings = {

        "/"(controller:'dashboard')
        
        "/organizations"             (controller: 'organizations', action: [GET: 'index', POST: 'store'])
        "/organizations/create"      (controller: 'organizations', action: 'create')
        "/organizations/$id/edit"    (controller: 'organizations', action: 'edit')
        "/organizations/$id"         (controller: 'organizations', action: [PUT: 'update', POST: 'update', DELETE: 'delete'])
        "/organizations/$id/restore" (controller: 'organizations', action: 'restore')

        "/contacts"             (controller: 'contacts', action: [GET: 'index', POST: 'store'])
        "/contacts/create"      (controller: 'contacts', action: 'create')
        "/contacts/$id/edit"    (controller: 'contacts', action: 'edit')
        "/contacts/$id"         (controller: 'contacts', action: [PUT: 'update', POST: 'update', DELETE: 'delete'])
        "/contacts/$id/restore" (controller: 'contacts', action: 'restore')

        "/users"             (controller: 'users', action: [GET: 'index', POST: 'storeUser'])
        "/users/create"      (controller: 'users', action: 'create')
        "/users/$id/edit"    (controller: 'users', action: 'edit')
        "/users/$id"         (controller: 'users', action: [PUT: 'updateUser', POST: 'updateUser', DELETE: 'delete'])
        "/users/$id/restore" (controller: 'users', action: 'restore')

        "/reports" (controller: 'report', action: 'index')

        /*
         * Use a named route to make requests like
         * /img/users/user-1-123.jpg?w=60&h=60&fit=crop
         * work.
         */
        name image: "/img/$path**"(controller: 'images')

        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
