/*
* Copyright 2024 original authors
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

import com.fasterxml.jackson.databind.ObjectMapper
import dev.nicklasw.squiggly.context.provider.SquigglyFilterHolder
import dev.nicklasw.squiggly.util.SquigglyUtils
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Bean
import jakarta.inject.Named

/**
 * A mapper for mapping domain objects to public data objects.
 * @author Mattias Reichel
 * @since 3.2.2
 */
@Bean
@CompileStatic
class PublicDataMapper {

    private final ObjectMapper objectMapper

    PublicDataMapper(@Named('publicDataMapper') ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
    }

    List<Map> map(List<PublicData> objectsToMap, List<String> propertiesToShow) {
        objectsToMap.collect({ map(it, propertiesToShow) })
    }

    Map map(PublicData objectToMap, List<String> propertiesToShow) {
        // TODO: Remove squiggly dependency
        // Using Squiggly here because it can resolve "deep" properties of Object (eg. contact['organization.name'])
        // Squiggly is setup to use a ThreadLocal to set/get the filter with this object mapper
        SquigglyFilterHolder.filter = propertiesToShow.join ','
        SquigglyUtils.objectify(objectMapper, objectToMap, Map)
    }
}
