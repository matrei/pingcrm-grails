package pingcrm.i18n

import spock.lang.Specification

/**
 * Makes sure all application messages are translated.
 */
class MessageBundlesSpec extends Specification {

    private static final List<String> APPLICATION_MESSAGE_PREFIXES = ['ui.', 'flash.', 'user.', 'login.']

    void 'the #locale bundle has a translation for every application message'() {

        given: 'the application messages of the default and the translated bundle'
            var defaultCodes = applicationMessageCodes('messages.properties')
            var translatedCodes = applicationMessageCodes("messages_${locale}.properties")

        expect: 'both bundles contain the same message codes'
            defaultCodes - translatedCodes == [] as Set
            translatedCodes - defaultCodes == [] as Set

        where:
            locale << ['sv']
    }

    private static Set<String> applicationMessageCodes(String bundleName) {
        var bundle = new Properties()
        new File("grails-app/i18n/$bundleName").withInputStream { bundle.load(it) }
        bundle.stringPropertyNames().findAll { code ->
            APPLICATION_MESSAGE_PREFIXES.any { code.startsWith(it) }
        } as Set
    }
}
