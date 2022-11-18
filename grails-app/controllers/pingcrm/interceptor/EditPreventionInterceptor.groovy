package pingcrm.interceptor


import groovy.transform.CompileStatic
import pingcrm.UserService

/**
 * This interceptor prevents anyone from editing or deleting the "demo user" (johndoe@example.com).
 * As this application is supposed to run openly as a demo where anyone is allowed to log in we need
 * this prevention or anyone could delete the demo user and prevent the next person to log in.
 */
@CompileStatic
class EditPreventionInterceptor {

    UserService userService

    EditPreventionInterceptor() {
        match controller: 'users', action: 'updateUser'
        match controller: 'users', action: 'delete'
    }

    boolean before() {

        def id = params.long 'id'
        if(userService.isDemoUser id) {
            flash.error = "${actionName == 'delete' ? 'Deleting' : 'Updating'} the demo user is not allowed."
            redirect controller: 'users', action: 'edit', id: id
            return false
        }
        true
    }
}
