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
import pingcrm.AppService

/**
 * This interceptor helps passing on data (in Inertia lingo 'shared data') to the client that is almost always
 * needed so we don't have to do it manually in every controller.
 *
 * In this case we send details about the user (if logged in) and flash messages.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class SharedDataInterceptor {

    AppService appService

    SharedDataInterceptor() {
        match controller: '*'
    }

    boolean before() {

        def sharedData = inertiaSharedData

        def user = appService.currentUser
        if(user) {
            sharedData << [
                auth: [
                    user: [
                        id: user.id,
                        account: [ name: user.account.name ],
                        first_name: user.firstName,
                        last_name: user.lastName,
                        email: user.email,
                        owner: user.owner
                    ]
                ]
            ]
        }

        sharedData << [
            flash: [
                success: flash.success,
                error: flash.error
            ]
        ]

        inertiaSharedData = sharedData

        true
    }
}
