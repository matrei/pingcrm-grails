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

import pingcrm.User
import groovy.transform.CompileStatic
import jakarta.inject.Inject
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest
import org.springframework.web.multipart.support.StandardServletMultipartResolver
import pingcrm.UserService

import javax.servlet.http.HttpServletRequest

/**
 * A custom MultipartResolver that enabled to show controlled error messages when the max upload file limit is reached.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class MaxFileUploadSizeResolver extends StandardServletMultipartResolver {

    private final UserService userService

    @Value('${grails.controllers.upload.maxFileSize}')
    long maxUploadSize

    @Inject
    MaxFileUploadSizeResolver(UserService userService) {
        this.userService = userService
    }

    @Override
    MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) {

        try {
            return super.resolveMultipart(request)
        }
        catch (MaxUploadSizeExceededException e) {

            // The client will have checked the file size and not send any file if it is too large.
            // This code will handle the case where client validation is broken or a user circumvents the client validation.
            def multipartParams = new LinkedHashMap<String, String[]>()

            if(request.requestURL =~ /\/users$/) {
                multipartParams.serverSaysPhotoTooLarge = [maxUploadSize]
            }
            else if(request.requestURL =~ /\/users\/\d+/) {

                // As the max upload size was hit, we can't get the other params that was sent.
                // Get the id from the url instead
                def url = request.requestURL.toString()
                def id = url.substring(url.lastIndexOf('/') + 1) as Long

                // Load the data from the db
                User user = userService.get(id, true)

                // Set the params from the loaded user
                multipartParams.id = [id]
                multipartParams.firstName = [user.firstName]
                multipartParams.lastName = [user.lastName]
                multipartParams.email = [user.email]
                multipartParams.owner = [user.owner ? '1' : '0']
                multipartParams.serverSaysPhotoTooLarge = [maxUploadSize]
            } else {
                throw e
            }
            return new DefaultMultipartHttpServletRequest(request, new LinkedMultiValueMap<String, MultipartFile>(), multipartParams, new LinkedHashMap<String, String>())
        }
    }
}
