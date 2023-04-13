/*
 * Copyright 2022-2023 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pingcrm.controller

import grails.plugin.springsecurity.annotation.Secured
import groovy.transform.CompileStatic
import jakarta.inject.Inject
import pingcrm.AppService
import pingcrm.Paginator
import pingcrm.User
import pingcrm.UserService
import pingcrm.config.UploadConfig

import javax.servlet.http.HttpServletRequest

/**
 * A CRUD controller for users.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
@Secured('IS_AUTHENTICATED_REMEMBERED')
class UsersController extends AppController<User> {

    private final UserService userService
    private final UploadConfig uploadConfig

    final List<String> indexProperties = ['id', 'name', 'email', 'owner', 'photo', 'deleted']
    final List<String> editProperties = ['id', 'firstName', 'lastName', 'email', 'owner', 'photo', 'deleted']
    final List<String> filterNames = ['search', 'trashed', 'role']

    static final Map allowedMethods = [
        index: 'GET',
        create: 'GET',
        storeUser: 'POST',
        edit: 'GET',
        updateUser: ['PUT', 'POST'],
        delete: 'DELETE',
        restore: 'PUT'
    ]

    @Inject
    UsersController(AppService appService, UserService userService, UploadConfig uploadConfig) {
        super(User, appService)
        this.userService = userService
        this.uploadConfig = uploadConfig
    }

    @Override
    def index(Paginator ignore) {

        def filters = params.subMap filterNames
        def users = appService.list User, null, filters
        def usersPublicData = users*.publicData indexProperties
        usersPublicData = usersPublicData.collect {userData -> addPhotoUrl userData, [w: 40, h: 40, fit: 'crop'] }

        renderInertia indexComponent, [users: usersPublicData, filters: filters]
    }

    @Override
    def create() {

        addToModel([
            maxPhotoSize: uploadConfig.maxFileSize,
            testServerMaxUploadSizeValidation: shouldServerMaxUploadSizeValidationBeTested(request)
        ])
        super.create()
    }

    @Override
    def edit(Long id) {

        def user = findOrRedirect id
        if(!user) return null

        def userPublicData = user.publicData editProperties
        addPhotoUrl userPublicData, [w: 60, h: 60, fit: 'crop']

        renderInertia editComponent, [
            user: userPublicData,
            maxPhotoSize: uploadConfig.maxFileSize,
            testServerMaxUploadSizeValidation: shouldServerMaxUploadSizeValidationBeTested(request)
        ]
    }

    def storeUser(UserCommand userCmd) {

        // Validate the photo field first. If the max upload size was hit, no other fields will be available
        if(!userCmd.validate(['photo'])) { chain action: 'create', model: [errors: renderErrors(userCmd.errors)]; return }

        if(!userCmd.validate()) { chain action: 'create', model: [errors: renderErrors(userCmd.errors)]; return }

        def user = userService.createUser userCmd
        if(userCmd.hasErrors()) { chain action: 'create', model: [errors: renderErrors(userCmd.errors)]; return }

        if(!user) {
            flash.error = 'Failed to create user.'
            redirect action: 'create'; return
        }

        if(user.hasErrors()) { chain action: 'create', model: [errors: renderErrors(user.errors)]; return }

        flash.success = 'User created.'
        redirect action: 'index'
    }

    def updateUser(UpdateUserCommand userCmd) {

        if(!userCmd.validate()) { chain action: 'edit', id: userCmd.id, model: [errors: renderErrors(userCmd.errors)]; return }

        def user = userService.updateUser userCmd
        if(userCmd.hasErrors()) { chain action: 'edit', id: userCmd.id, model: [errors: renderErrors(userCmd.errors)]; return }

        if(user.hasErrors()) {
            if(user.errors.fieldErrors) {
                chain action: 'edit', id: userCmd.id, model: [errors: renderErrors(user.errors)]; return
            } else {
                flash.error = 'User not found.'
                redirect action: 'index'; return
            }
        }

        flash.success = 'User updated.'
        redirect action: 'edit', id: userCmd.id
    }

    @Override
    def delete(Long id) {

        if(isCurrentUser id) {
            if(appService.delete User, id) {
                session.invalidate()
                redirect controller: 'login'; return
            } else {
                flash.error = 'Failed to delete user.'
                redirect action: 'edit', id: id; return
            }
        }

        if(appService.delete User, id) {
            userService.invalidateSessionsForUserId id
            flash.success = 'User deleted.'
        } else {
            flash.error = 'Failed to delete user.'
        }

        redirect action: 'edit', id: id
    }

    private boolean isCurrentUser(Long id) { userService.isCurrentUser id }

    private Map addPhotoUrl(Map user, Map photoParams) {

        if(user.photo) {
            def photoUrl = grailsLinkGenerator.link mapping: 'image', params: photoParams + [path: user.photo]
            user.photo = photoUrl
        }

        user
    }

    private static boolean shouldServerMaxUploadSizeValidationBeTested(HttpServletRequest request) {
        request.getParameter('testServerMaxUploadSizeValidation') != null
    }
}