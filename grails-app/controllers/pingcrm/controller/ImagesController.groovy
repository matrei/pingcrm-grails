/*
 * Copyright 2022-present original authors
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

import java.awt.image.BufferedImage
import java.time.Duration

import javax.imageio.ImageIO

import groovy.transform.CompileStatic

import jakarta.inject.Inject

import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

import pingcrm.image.ImageProcessor
import pingcrm.services.FileService

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

/**
 * A controller that renders user profile images.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class ImagesController {

    // An uploaded image gets a new file name, and the size is part of the URL,
    // so the image at a URL never changes and the browser can keep it.
    // It is private, as the images are only shown to logged-in users.
    private static final String CACHE_CONTROL = CacheControl.maxAge(Duration.ofDays(365))
            .cachePrivate()
            .immutable()
            .headerValue

    private final FileService fileService

    static final String defaultAction = 'thumbnail'
    static final Map allowedMethods = [thumbnail: 'GET']

    @Inject
    ImagesController(FileService fileService) {
        this.fileService = fileService
    }

    def thumbnail(ImageThumbnailCommand cmd) {

        if (!cmd.validate()) { render(status: UNPROCESSABLE_ENTITY); return }

        try {

            var image= fileService.createImageThumbnail(cmd.path, [width: cmd.w, height: cmd.h, fit: cmd.fit, quality: cmd.quality])

            var contentType = 'image/jpeg'
            var formatName = 'JPG'

            if (image.type == BufferedImage.TYPE_INT_ARGB) {
                contentType = 'image/png'
                formatName = 'PNG'
            }

            response.contentType = contentType
            response.setHeader(HttpHeaders.CACHE_CONTROL, CACHE_CONTROL)
            ImageIO.write(image, formatName, response.outputStream)

        }
        catch (FileNotFoundException ignore) { render(status: NOT_FOUND) }
        catch (Exception ignore) { render(status: INTERNAL_SERVER_ERROR) }
    }
}

@GrailsCompileStatic
class ImageThumbnailCommand implements Validateable {

    String path, fit, quality
    Integer w,  h

    @SuppressWarnings('unused')
    static final constraints = {
        w(min: 20, max: 480)
        h(min: 20, max: 480)
        fit(nullable: true, inList: ImageProcessor.ResizingMode.validValues)
        quality(nullable: true, inList: ImageProcessor.ScalingQuality.validValues)
    }
}
