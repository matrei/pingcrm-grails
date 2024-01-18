package pingcrm.config

import groovy.transform.CompileStatic
import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.convert.format.MapFormat

@CompileStatic
@ConfigurationProperties(PREFIX)
class GitInfo {

    private static final String PREFIX = 'git'

    String branch

    @MapFormat(transformation = MapFormat.MapTransformation.FLAT)
    Map<String,String> build

    @MapFormat(transformation = MapFormat.MapTransformation.FLAT)
    Map<String,String> commit
}
