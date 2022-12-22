package pingcrm.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties('grails')
class GrailsInfo {

    String profile

}
