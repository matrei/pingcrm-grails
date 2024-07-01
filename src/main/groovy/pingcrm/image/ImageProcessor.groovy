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
package pingcrm.image

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifIFD0Directory
import groovy.transform.CompileStatic

import java.awt.image.BufferedImage

/**
 * A trait for image processing classes.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
trait ImageProcessor {

    abstract BufferedImage resize(BufferedImage src, int targetWidth, int targetHeight, ResizingMode resizeMode, ScalingQuality scalingQuality)
    abstract BufferedImage rotate(BufferedImage src, Rotation rotation)

    Rotation getImageRotation(File file) {

        def rotation = Rotation.NONE

        def metadata = ImageMetadataReader.readMetadata(file)
        if (metadata.containsDirectoryOfType(ExifIFD0Directory)) {

            def exifIFD0 = metadata.getDirectoriesOfType(ExifIFD0Directory)
            def orientation = exifIFD0.find({ it.containsTag(ExifIFD0Directory.TAG_ORIENTATION) })?.getInt(ExifIFD0Directory.TAG_ORIENTATION)

            if (orientation) {
                switch (orientation) {
                    case 6: rotation = Rotation.CW_90; break // [Exif IFD0] Orientation - Right side, top (Rotate 90 CW)
                    case 3: rotation = Rotation.CW_180; break // [Exif IFD0] Orientation - Bottom, right side (Rotate 180)
                    case 8: rotation = Rotation.CW_270; break // [Exif IFD0] Orientation - Left side, bottom (Rotate 270 CW)
                    default: break
                }
            }
        }

        return rotation
    }

    enum Rotation {

        NONE('0'),
        CW_90('90'),
        CW_180('180'),
        CW_270('270')

        private final String value

        private Rotation(String value) { this.value = value }
        static Rotation get(Object value) { values().find({ it.value == value }) ?: NONE }
        static List<String> getValidValues() { values()*.value }
        String getValue() { value }
        String toString() { value }
    }

    enum ResizingMode {

        AUTOMATIC('auto'),
        FIT_EXACT('exact'),
        FIT_TO_WIDTH('fity'),
        FIT_TO_HEIGHT('fitx'),
        CROP('crop')

        private final String value

        private ResizingMode(String value) { this.value = value }
        static ResizingMode get(Object value) { values().find { it.value == value } ?: AUTOMATIC }
        static List<String> getValidValues() { values()*.value }
        String getValue() { value }
        String toString() { value }
    }

    enum ScalingQuality {

        AUTOMATIC('auto'),
        SPEED('speed'),
        BALANCED('balanced'),
        QUALITY('quality'),
        ULTRA_QUALITY('ultra-quality')

        private final String value

        private ScalingQuality(String value) { this.value = value }
        static ScalingQuality get(Object value) { values().find({ it.value == value }) ?: AUTOMATIC }
        static List<String> getValidValues() { values()*.value }
        String getValue() { value }
        String toString() { value }
    }
}