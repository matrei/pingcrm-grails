package pingcrm

import com.fasterxml.jackson.databind.ObjectMapper
import dev.nicklasw.squiggly.context.provider.SquigglyFilterHolder
import dev.nicklasw.squiggly.util.SquigglyUtils
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Bean
import jakarta.inject.Named

@Bean
@CompileStatic
class PublicDataMapper {

    private final ObjectMapper objectMapper

    PublicDataMapper(@Named('publicDataMapper') ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
    }

    List<Map> map(List<PublicData> objectsToMap, List<String> propertiesToShow) {
        objectsToMap.collect { map(it, propertiesToShow) }
    }

    Map map(PublicData objectToMap, List<String> propertiesToShow) {
        // TODO: Remove squiggly dependency
        // Using Squiggly here because it can resolve "deep" properties of Object (eg. contact['organization.name'])
        // Squiggly is setup to use a ThreadLocal to set/get the filter with this object mapper
        SquigglyFilterHolder.filter = propertiesToShow.join ','
        SquigglyUtils.objectify(objectMapper, objectToMap, Map)
    }
}
