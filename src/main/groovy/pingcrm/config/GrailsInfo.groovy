package pingcrm.config

import groovy.transform.CompileStatic
import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties('grails')
@CompileStatic
class GrailsInfo {

    String profile

}
