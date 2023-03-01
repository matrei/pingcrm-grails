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
package pingcrm

import groovy.transform.CompileStatic
import pingcrm.image.ImageProcessor

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * A service for handling images in the application.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class ImageService {

    ImageProcessor imageProcessor

    BufferedImage resizeImage(File file, Map options) throws IOException {

        def rotation = imageProcessor.getImageRotation file
        def scalingQuality = ImageProcessor.ScalingQuality.get options.quality
        def resizingMode = ImageProcessor.ResizingMode.get options.fit

        def width = (options.width ?: 40) as int
        def height = (options.height ?: 40) as int

        def inputImage = ImageIO.read file

        if(rotation != rotation.NONE) inputImage = imageProcessor.rotate inputImage, rotation
        def outputImage = imageProcessor.resize inputImage, width, height, resizingMode, scalingQuality

        outputImage
    }
}