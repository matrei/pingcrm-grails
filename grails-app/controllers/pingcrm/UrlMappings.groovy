/*
 * Copyright 2022-2023 original authors
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

/**
 * A class that defines the URL mappings for the application.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
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
