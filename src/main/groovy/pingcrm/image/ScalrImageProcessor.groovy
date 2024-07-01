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

import groovy.transform.CompileStatic
import jakarta.inject.Singleton
import org.imgscalr.Scalr

import java.awt.*
import java.awt.image.BufferedImage

/**
 * Implementation of ImageProcessor that uses Scalr
 * and also handles fit mode: crop
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@Singleton
@CompileStatic
class ScalrImageProcessor extends Scalr implements ImageProcessor {

    @Override
    BufferedImage resize(BufferedImage src, int targetWidth, int targetHeight,
                         ResizingMode resizingMode = ResizingMode.AUTOMATIC,
                         ScalingQuality scalingQuality = ScalingQuality.AUTOMATIC) {

        long t = System.currentTimeMillis()

        if (src == null) throw new IllegalArgumentException('src cannot be null')
        if (targetWidth < 0) throw new IllegalArgumentException('targetWidth must be >= 0')
        if (targetHeight < 0) throw new IllegalArgumentException('targetHeight must be >= 0')

        def scalrScalingMethod = getScalrMethod(scalingQuality)

        BufferedImage result = null

        int currentWidth = src.width
        int currentHeight = src.height

        // <= 1 is a square or landscape-oriented image, > 1 is a portrait.
        def ratio = currentHeight / currentWidth

        if (DEBUG) log(0,'Resizing Image [size=%dx%d, resizingMode=%s, orientation=%s, ratio(H/W)=%f] to [targetSize=%dx%d]', currentWidth, currentHeight, resizingMode, (ratio <= 1 ? 'Landscape/Square' : 'Portrait'), ratio, targetWidth, targetHeight)

        if (resizingMode != ResizingMode.FIT_EXACT) {

            if ((ratio <= 1 && resizingMode == ResizingMode.AUTOMATIC) || (resizingMode == ResizingMode.FIT_TO_WIDTH)) {

                if (targetWidth == currentWidth) return src

                // Save for detailed logging (this is cheap).
                int originalTargetHeight = targetHeight

                /*
                 * Landscape or Square Orientation: Ignore the given height and
                 * re-calculate a proportionally correct value based on the
                 * targetWidth.
                 */
                targetHeight = roundedInt(targetWidth * ratio)

                if (DEBUG && originalTargetHeight != targetHeight) log(1,'Auto-Corrected targetHeight [from=%d to=%d] to honor image proportions.', originalTargetHeight, targetHeight)

            } else if (resizingMode == ResizingMode.CROP) { // CROP

                // If already right size return
                if (targetWidth == currentWidth && targetHeight == currentHeight) return src

                int originalTargetWidth = targetWidth
                int originalTargetHeight = targetHeight

                // Do we need to crop
                int cropWidth = currentWidth
                int cropHeight = currentHeight
                if (ratio != 1) {
                    // Calculate crop x offset and new original width to use when cropping
                    int xOffset = 0
                    if (ratio < 1) {
                        cropWidth = roundedInt(currentWidth * ratio)
                        xOffset = roundedInt((currentWidth - cropWidth) / 2)
                    }

                    // Calculate crop y offset and new original height to use when cropping
                    int yOffset = 0
                    if (ratio > 1) {
                        cropHeight = roundedInt(currentHeight / ratio)
                        yOffset = roundedInt((currentHeight - cropHeight) / 2)
                    }
                    // Crop to fit within the target height and width
                    src = crop(src, xOffset, yOffset, cropWidth, cropHeight)
                }

                // Calculate resize
                def cropRatio = (targetWidth / cropWidth).max(targetHeight / cropHeight)
                targetWidth  = roundedInt(cropWidth * cropRatio)
                targetHeight = roundedInt(cropHeight * cropRatio)

                // If the calculated size is bigger than what was sent in, return what was sent in instead.
                targetWidth = targetWidth > originalTargetWidth ? originalTargetWidth : targetWidth
                targetHeight = targetHeight > originalTargetHeight ? originalTargetHeight : targetHeight

            } else {

                // First make sure we need to do any work in the first place
                if (targetHeight == currentHeight) return src

                // Save for detailed logging (this is cheap).
                int originalTargetWidth = targetWidth

                /*
                 * Portrait Orientation: Ignore the given width and re-calculate
                 * a proportionally correct value based on the targetHeight.
                 */
                targetWidth = roundedInt(targetHeight / ratio)

                if (DEBUG && originalTargetWidth != targetWidth) log(1, 'Auto-Corrected targetWidth [from=%d to=%d] to honor image proportions.', originalTargetWidth, targetWidth)
            }
        } else {
            if (DEBUG) log(1,'Resize Mode FIT_EXACT used, no width/height checking or re-calculation will be done.')
        }

        // If AUTOMATIC was specified, determine the real scaling method.
        if (scalrScalingMethod == Method.AUTOMATIC)
            scalrScalingMethod = determineScalingMethod(targetWidth, targetHeight, ratio.floatValue())

        if (DEBUG) log(1, 'Using Scaling Method: %s', scalrScalingMethod)

        // Now we scale the image
        if (scalrScalingMethod == Method.SPEED) {
            result = scaleImage(src, targetWidth, targetHeight, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
        } else if (scalrScalingMethod == Method.BALANCED) {
            result = scaleImage(src, targetWidth, targetHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        } else if (scalrScalingMethod == Method.QUALITY || scalrScalingMethod == Method.ULTRA_QUALITY) {
            /*
             * If we are scaling up (in either width or height - since we know
             * the image will stay proportional we just check if either are
             * being scaled up), directly using a single BICUBIC will give us
             * better results then using Chris Campbell's incremental scaling
             * operation (and take a lot less time).
             *
             * If we are scaling down, we must use the incremental scaling
             * algorithm for the best result.
             */
            if (targetWidth > currentWidth || targetHeight > currentHeight) {
                if (DEBUG) log(1,'QUALITY scale-up, a single BICUBIC scale operation will be used...')

                /*
                 * BILINEAR and BICUBIC look similar the smaller the scale jump
                 * upwards is, if the scale is larger BICUBIC looks sharper and
                 * less fuzzy. But most importantly we have to use BICUBIC to
                 * match the contract of the QUALITY rendering scalingMethod.
                 * This note is just here for anyone reading the code and
                 * wondering how they can speed their own calls up.
                 */
                result = scaleImage(src, targetWidth, targetHeight, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
            } else {
                if (DEBUG) log(1,'QUALITY scale-down, incremental scaling will be used...')

                /*
                 * Originally we wanted to use BILINEAR interpolation here
                 * because it takes 1/3rd the time that the BICUBIC
                 * interpolation does, however, when scaling large images down
                 * to most sizes bigger than a thumbnail we witnessed noticeable
                 * "softening" in the resultant image with BILINEAR that would
                 * be unexpectedly annoying to a user expecting a "QUALITY"
                 * scale of their original image. Instead BICUBIC was chosen to
                 * honor the contract of a QUALITY scale of the original image.
                 */
                result = scaleImageIncrementally(src, targetWidth, targetHeight, scalrScalingMethod, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
            }
        }

        if (DEBUG) log(0, 'Resized Image in %d ms', System.currentTimeMillis() - t)

        result
    }

    @Override
    BufferedImage rotate(BufferedImage src, Rotation rotation) {
        def scalrRotation = getScalrRotation(rotation)
        scalrRotation ? rotate(src, scalrRotation, null) : src
    }

    static Method getScalrMethod(ScalingQuality scalingQuality) {

        Method scalrMethod = Method.AUTOMATIC
        switch (scalingQuality) {
            case ScalingQuality.SPEED: scalrMethod = Method.SPEED; break
            case ScalingQuality.BALANCED: scalrMethod = Method.BALANCED; break
            case ScalingQuality.QUALITY: scalrMethod = Method.QUALITY; break
            case ScalingQuality.ULTRA_QUALITY: scalrMethod = Method.ULTRA_QUALITY; break
            default: break
        }
        scalrMethod
    }

    static Scalr.Rotation getScalrRotation(Rotation rotation) {

        Scalr.Rotation scalrRotation = null
        switch (rotation) {
            case Scalr.Rotation.CW_90: scalrRotation = Scalr.Rotation.CW_90; break
            case Scalr.Rotation.CW_180: scalrRotation = Scalr.Rotation.CW_180; break
            case Scalr.Rotation.CW_270: scalrRotation = Scalr.Rotation.CW_270; break
            default: break
        }
        scalrRotation
    }

    private static int roundedInt(BigDecimal value) {
        return value.round().intValue()
    }
}
