package pingcrm.config

import groovy.transform.CompileStatic
import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties('info.app')
@CompileStatic
class AppInfo {

    String name
    String version
    String grailsVersion

}
