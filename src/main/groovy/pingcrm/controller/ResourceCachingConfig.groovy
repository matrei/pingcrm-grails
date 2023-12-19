package pingcrm.controller

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

import java.util.concurrent.TimeUnit

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
