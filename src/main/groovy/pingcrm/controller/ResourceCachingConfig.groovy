/*
* Copyright 2023 original authors
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

package pingcrm.controller

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

import java.util.concurrent.TimeUnit

/**
 * Configures caching headers for static resources.
 * @author Mattias Reichel
 * @since 3.2.0
 */
@Configuration
@CompileStatic
class ResourceCachingConfig implements WebMvcConfigurer {

    private static final int ONE_YEAR = 31536000
    private static final int ONE_HOUR = 3600

    @Override
    void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Set caching header for javascript anc css bundled resources
        registry.addResourceHandler('/static/dist/js/**')
                .addResourceLocations('classpath:/public/dist/js/')
                .setCacheControl(CacheControl.maxAge(ONE_YEAR, TimeUnit.SECONDS).cachePublic())

        // Set caching header for favicon
        registry.addResourceHandler('/static/favicon.sv*')
                .addResourceLocations('classpath:/public/')
                .setCacheControl(CacheControl.maxAge(ONE_HOUR, TimeUnit.SECONDS).cachePublic())
    }
}
