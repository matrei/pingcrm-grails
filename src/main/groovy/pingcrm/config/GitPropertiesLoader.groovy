package pingcrm.config

import io.micronaut.context.env.PropertiesPropertySourceLoader
import io.micronaut.context.env.PropertySource
import io.micronaut.core.io.ResourceLoader

class GitPropertiesLoader extends PropertiesPropertySourceLoader {

    @Override
    Optional<PropertySource> load(String resourceName, ResourceLoader resourceLoader) {
        loadProperties('git.properties', resourceLoader)
    }

    private Optional<PropertySource> loadProperties(String resourceName, ResourceLoader resourceLoader) {
        def propertyMap = new HashMap<String,Object>()
        try (InputStream inputStream = resourceLoader.getResourceAsStream(resourceName).orElse(null)) {
            processInput(resourceName, inputStream, propertyMap)
            return Optional.of(PropertySource.of(resourceName, propertyMap))
        } catch(Exception ignore) {
            log.error('Error loading properties from resource: {}', resourceName)
            return Optional.empty()
        }
    }
}