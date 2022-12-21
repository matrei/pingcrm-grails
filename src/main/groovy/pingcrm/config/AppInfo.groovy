package pingcrm.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties('info.app')
class AppInfo {

    String name
    String version
    String grailsVersion

}
