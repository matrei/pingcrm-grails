package pingcrm

import groovy.transform.CompileStatic
import org.grails.plugins.web.taglib.ValidationTagLib
import org.springframework.validation.Errors

import javax.inject.Inject

@CompileStatic
trait ValidationMessageRenderer {

    @Inject ValidationTagLib g

    Map renderErrors(Errors errors) {
        errors.fieldErrors.collectEntries {[it.field, g.message(error: it)] }
    }
}