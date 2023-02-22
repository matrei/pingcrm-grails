package pingcrm

import com.fasterxml.jackson.databind.ObjectMapper
import dev.nicklasw.squiggly.Squiggly
import dev.nicklasw.squiggly.context.provider.ThreadLocalContextProvider
import groovy.transform.CompileStatic
import org.springframework.beans.factory.FactoryBean

@CompileStatic
class SquigglyObjectMapperFactory implements FactoryBean<ObjectMapper> {

    private final ObjectMapper objectMapper

    SquigglyObjectMapperFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper
    }

    @Override
    ObjectMapper getObject() throws Exception {
        Squiggly.init(objectMapper, new ThreadLocalContextProvider());
    }

    @Override Class<?> getObjectType() { ObjectMapper }
}
