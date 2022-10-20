package pingcrm.interceptor


import groovy.transform.CompileStatic
import pingcrm.AppService

/**
 * This interceptor helps us pass on data to the client side that we almost always want to send so we don't
 * have to do it manually everywhere. In this case we send details about the user (if logged in) and flash messages.
 */
@CompileStatic
class SharedDataInterceptor {

    AppService appService

    SharedDataInterceptor() { matchAll() }

    boolean before() {

        def sharedData = inertiaSharedData

        def user = appService.currentUser
        if(user) {
            sharedData << [
                auth: [
                    user: [
                        id: user.id,
                        account: [ name: user.account.name ],
                        first_name: user.firstName,
                        last_name: user.lastName,
                        email: user.email,
                        owner: user.owner
                    ]
                ]
            ]
        }

        sharedData << [
            flash: [
                success: flash.success,
                error: flash.error
            ]
        ]

        inertiaSharedData = sharedData

        true
    }
}
