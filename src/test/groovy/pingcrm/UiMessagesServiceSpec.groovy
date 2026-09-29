package pingcrm

import org.springframework.context.support.ResourceBundleMessageSource

import spock.lang.Specification

class UiMessagesServiceSpec extends Specification {

    private final UiMessagesService service = new UiMessagesService(
            messageSource: new ResourceBundleMessageSource(
                    basenames: ['messages'] as String[],
                    defaultEncoding: 'UTF-8',
                    fallbackToSystemLocale: false
            )
    )

    void 'only UI messages are provided'() {

        when: 'getting the UI messages'
            def messages = service.messagesFor(Locale.ENGLISH)

        then: 'all messages are UI messages'
            messages
            messages.keySet().every { it.startsWith('ui.') }
    }

    void 'UI messages are resolved in the given locale'() {

        expect: 'the messages to be in the expected language'
            service.messagesFor(locale)['ui.login.welcome'] == welcome

        where:
            locale                    | welcome
            Locale.ENGLISH            | 'Welcome Back!'
            Locale.forLanguageTag('sv') | 'Välkommen tillbaka!'
            Locale.GERMAN             | 'Welcome Back!'
    }

    void 'placeholders are left for the client to fill in'() {

        expect:
            service.messagesFor(Locale.ENGLISH)['ui.flash.formErrors.other'] == 'There are {0} form errors.'
    }
}
