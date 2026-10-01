/*
* Copyright 2024-present original authors
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

import java.time.temporal.Temporal

import groovy.transform.CompileStatic

import org.springframework.stereotype.Component

/**
 * A mapper for mapping domain objects to public data objects.
 * <p>
 * Only the given properties are selected. Nested properties are selected
 * with dot notation, e.g. {@code organization.name} results in
 * {@code [organization: [name: 'Acme']]} (or {@code [organization: null]}
 * when there is no organization).
 * <p>
 * A property path must end at a simple value (e.g. a string, number, boolean,
 * enum or date). Selecting an associated object as a whole is not allowed,
 * as that could expose all of its properties to the client side.
 *
 * @author Mattias Reichel
 * @since 3.2.2
 */
@Component
@CompileStatic
class PublicDataMapper {

    private static final List<Class> SIMPLE_TYPES = [CharSequence, Number, Boolean, Character, Enum, Temporal, Date]

    List<Map> map(List<PublicData> objectsToMap, List<String> propertiesToShow) {
        var propertyPaths = toPropertyPaths(propertiesToShow)
        objectsToMap.collect {
            mapPropertyPaths(it, propertyPaths)
        }
    }

    Map map(PublicData objectToMap, List<String> propertiesToShow) {
        mapPropertyPaths(objectToMap, toPropertyPaths(propertiesToShow))
    }

    /**
     * Splits the properties into paths once, so it is not done for every mapped object.
     */
    private static List<List<String>> toPropertyPaths(List<String> properties) {
        properties.collect {
            it.tokenize('.')
        }
    }

    private static Map mapPropertyPaths(Object source, List<List<String>> propertyPaths) {
        var publicData = [:]
        propertyPaths.each {
            put(publicData, source, it, 0)
        }
        publicData
    }

    private static void put(Map target, Object source, List<String> propertyPath, int index) {
        var name = propertyPath[index]
        var value = source[name]
        if (index == propertyPath.size() - 1) {
            if (!isSimpleValue(value)) {
                var path = propertyPath.join('.')
                throw new IllegalArgumentException(
                        "Property '$path' of ${source.class.simpleName} is not a simple value. " +
                        "Select its nested properties instead, e.g. '${path}.id'."
                )
            }
            target[name] = value
            return
        }
        if (value == null) {
            target[name] = null
            return
        }
        var nested = (target[name] ?: [:]) as Map
        target[name] = nested
        put(nested, value, propertyPath, index + 1)
    }

    private static boolean isSimpleValue(Object value) {
        value == null || SIMPLE_TYPES.any { it.isInstance(value) }
    }
}
