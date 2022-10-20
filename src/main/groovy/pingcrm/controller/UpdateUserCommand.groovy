package pingcrm.controller

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class UpdateUserCommand extends UserCommand {

    static constraints = {
        password nullable: true
    }
}
