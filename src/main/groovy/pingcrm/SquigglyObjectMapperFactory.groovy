package pingcrm

import com.fasterxml.jackson.databind.ObjectMapper
import dev.nicklasw.squiggly.Squiggly
import dev.nicklasw.squiggly.context.provider.ThreadLocalContextProvider
import org.springframework.beans.factory.FactoryBean

class SquigglyObjectMapperFactory implements FactoryBean<ObjectMapper> {

    @Override
    ObjectMapper getObject() throws Exception {
        Squiggly.init(new ObjectMapper(), new ThreadLocalContextProvider());
    }

    @Override Class<?> getObjectType() { ObjectMapper }
}
