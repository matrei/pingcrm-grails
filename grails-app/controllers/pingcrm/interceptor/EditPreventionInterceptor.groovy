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
package pingcrm.interceptor

import groovy.transform.CompileStatic
import pingcrm.UserService

/**
 * This interceptor prevents anyone from editing or deleting the "demo user" (johndoe@example.com).
 * As this application is supposed to run openly as a demo where anyone is allowed to log in we need
 * this prevention or anyone could delete the demo user and prevent the next person to log in.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class EditPreventionInterceptor {

    UserService userService

    EditPreventionInterceptor() {
        match controller: 'users', action: 'updateUser'
        match controller: 'users', action: 'delete'
    }

    boolean before() {

        def id = params.long 'id'
        if(userService.isDemoUser id) {
            flash.error = "${actionName == 'delete' ? 'Deleting' : 'Updating'} the demo user is not allowed."
            redirect controller: 'users', action: 'edit', id: id
            return false
        }
        true
    }
}
