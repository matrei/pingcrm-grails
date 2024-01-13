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

import groovy.transform.CompileStatic

/**
 * Used to shield developers from accidentally sending secret or unnecessary data to the client side with the
 * Inertia JSON responses. The default behaviour is to not select any properties at all.
 *
 * Implementing classes may override the getPublicProperties() method to specify which properties to select.
 * It is also possible to specify which properties to select when calling the publicData methods on a case by case basis.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
interface PublicData {

    /**
     * The default properties to select when calling the publicData methods.
     * @return a list of property names
     */
    default List<String> getPublicProperties() { Collections.emptyList() }
}