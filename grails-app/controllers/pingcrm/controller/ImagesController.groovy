/*
 * Copyright 2022-2024 original authors
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

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.Validateable
import groovy.transform.CompileStatic
import jakarta.inject.Inject
import pingcrm.services.FileService
import pingcrm.image.ImageProcessor

import static org.springframework.http.HttpStatus.*

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * A controller that renders user profile images.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
@Secured('IS_AUTHENTICATED_REMEMBERED')
class ImagesController {

    private final FileService fileService

    static final String defaultAction = 'thumbnail'
    static final Map allowedMethods = [thumbnail: 'GET']

    @Inject
    ImagesController(FileService fileService) {
        this.fileService = fileService
    }

    def thumbnail(ImageThumbnailCommand cmd) {

        if (!cmd.validate()) { render status: UNPROCESSABLE_ENTITY; return }

        try {

            def image= fileService.createImageThumbnail cmd.path, [width: cmd.w, height: cmd.h, fit: cmd.fit, quality: cmd.quality]

            def contentType = 'image/jpg'
            def formatName = 'JPG'

            if (image.type == BufferedImage.TYPE_INT_ARGB) {
                contentType = 'image/png'
                formatName = 'PNG'
            }

            response.contentType = contentType
            ImageIO.write image, formatName, response.outputStream

        }
        catch (FileNotFoundException ignore) { render status: NOT_FOUND }
        catch (Exception ignore) { render status: INTERNAL_SERVER_ERROR }
    }
}

@GrailsCompileStatic
class ImageThumbnailCommand implements Validateable {

    String path, fit, quality
    Integer w,  h

    @SuppressWarnings('unused')
    static final constraints = {
        w min: 20, max: 480
        h min: 20, max: 480
        fit nullable: true, inList: ImageProcessor.ResizingMode.validValues
        quality nullable: true, inList: ImageProcessor.ScalingQuality.validValues
    }
}
