/*
 * Copyright 2022-present original authors
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

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import dev.nicklasw.squiggly.Squiggly
import dev.nicklasw.squiggly.context.provider.ThreadLocalContextProvider

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.lang.Nullable

/**
 * A config class that initializes the Squiggly-based ObjectMapper.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@Configuration
@CompileStatic
class SquigglyObjectMapperFactory {

    /**
     * This is a Jackson ObjectMapper that in cooperation with Squiggly (https://github.com/NicklasWallgren/squiggly)
     * will help us select which properties to send to the client side.
     */
    @Bean
    @Qualifier('publicDataMapper')
    ObjectMapper getObjectMapper(@Nullable JsonFactory jsonFactory) throws Exception {
        def objectMapper = jsonFactory ? new ObjectMapper(jsonFactory) : new ObjectMapper()
        Squiggly.init(objectMapper, new ThreadLocalContextProvider())
    }
}
