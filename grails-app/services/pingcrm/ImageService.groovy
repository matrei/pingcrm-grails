package pingcrm


import groovy.transform.CompileStatic
import pingcrm.image.ImageProcessor

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

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