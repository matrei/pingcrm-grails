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
package pingcrm

/**
 * A class that defines the URL mappings for the application.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
class UrlMappings {

    static final Closure mappings = {

        "/" (controller: 'dashboard', action: [GET: 'index'])
        "/about" (controller: 'about', action: [GET: 'index'])
        "/reports" (controller: 'report', action: [GET: 'index'])

        "/organizations"             (controller: 'organizations', action: [GET: 'index', POST: 'store'])
        "/organizations/create"      (controller: 'organizations', action: [GET: 'create'])
        "/organizations/$id/edit"    (controller: 'organizations', action: [GET: 'edit'])
        "/organizations/$id"         (controller: 'organizations', action: [PUT: 'update', POST: 'update', DELETE: 'delete'])
        "/organizations/$id/restore" (controller: 'organizations', action: 'restore')

        "/contacts"             (controller: 'contacts', action: [GET: 'index', POST: 'store'])
        "/contacts/create"      (controller: 'contacts', action: [GET: 'create'])
        "/contacts/$id/edit"    (controller: 'contacts', action: [GET: 'edit'])
        "/contacts/$id"         (controller: 'contacts', action: [PUT: 'update', POST: 'update', DELETE: 'delete'])
        "/contacts/$id/restore" (controller: 'contacts', action: 'restore')

        "/users"             (controller: 'users', action: [GET: 'index', POST: 'storeUser'])
        "/users/create"      (controller: 'users', action: [GET: 'create'])
        "/users/$id/edit"    (controller: 'users', action: [GET: 'edit'])
        "/users/$id"         (controller: 'users', action: [PUT: 'updateUser', POST: 'updateUser', DELETE: 'delete'])
        "/users/$id/restore" (controller: 'users', action: 'restore')

        "/img/$path**" (controller: 'images', action: 'thumbnail')

        "/test-500-error" (controller: 'errorTest', action: 'generateInternalServerError')

        "/error" (view: '/error')
        "403" (controller: 'errorHandling', action: 'forbidden')
        "404" (controller: 'errorHandling', action: 'notFound')
        "419" (controller: 'errorHandling', action: 'csrfTokenMismatch')
        "500" (controller: 'errorHandling', action: 'internalServerError')
        "503" (controller: 'errorHandling', action: 'serviceUnavailable')
    }
}
