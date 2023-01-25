package pingcrm

import com.fasterxml.jackson.databind.ObjectMapper
import dev.nicklasw.squiggly.context.provider.SquigglyFilterHolder
import dev.nicklasw.squiggly.util.SquigglyUtils
import grails.util.Holders
import groovy.transform.CompileStatic

/**
 * Used to shield developers from accidentally sending secret or unnecessary data to the client side with the
 * Inertia JSON responses. The default behaviour is to not select any properties at all.
 *
 * Implementing classes may override the getPublicProperties() method to specify which properties to select.
 * It is also possible to specify which properties to select when calling the publicData methods on a case by case basis.
 */
@CompileStatic
trait PublicData {

    // Using def instead of explicit type to prevent GORM from handling this property
    // if the trait is implemented by Grails domain classes.
    def objectMapper = Holders.grailsApplication.mainContext.getBean('publicDataMapper')

    private static final String[] noProperties = [] as String[]

    String[] getPublicProperties() { noProperties }

    Map publicData(String ...propertiesToShow) {
        String[] props = propertiesToShow ?: publicProperties
        // Squiggly is setup to use a ThreadLocal to set/get the filter with this object mapper
        SquigglyFilterHolder.filter = props.join(',')
        SquigglyUtils.objectify(objectMapper as ObjectMapper, this, Map)
    }

    Map publicData(List<String> propertiesToShow) {
        publicData(propertiesToShow as String[])
    }
}