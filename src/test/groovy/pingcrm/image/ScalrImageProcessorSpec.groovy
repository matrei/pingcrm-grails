package pingcrm.image

import java.awt.image.BufferedImage

import org.imgscalr.Scalr

import spock.lang.Specification

import pingcrm.image.ImageProcessor.Rotation

class ScalrImageProcessorSpec extends Specification {

    private static final int MARKER = 0xFFFF0000

    private final ScalrImageProcessor imageProcessor = new ScalrImageProcessor()

    void 'rotation #rotation is mapped to Scalr rotation #scalrRotation'() {

        expect:
            ScalrImageProcessor.toScalrRotation(rotation) == scalrRotation

        where:
            rotation        | scalrRotation
            Rotation.NONE   | null
            Rotation.CW_90  | Scalr.Rotation.CW_90
            Rotation.CW_180 | Scalr.Rotation.CW_180
            Rotation.CW_270 | Scalr.Rotation.CW_270
    }

    void 'an image is rotated #rotation'() {

        given: 'a landscape image with a marked top left pixel'
            var image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB)
            image.setRGB(0, 0, MARKER)

        when: 'rotating the image'
            var rotated = imageProcessor.rotate(image, rotation)

        then: 'the image has the expected dimensions'
            rotated.width == width
            rotated.height == height

        and: 'the marked pixel has moved to the expected corner'
            rotated.getRGB(markerX, markerY) == MARKER

        where:
            rotation        | width | height | markerX | markerY
            Rotation.CW_90  | 10    | 20     | 9       | 0
            Rotation.CW_180 | 20    | 10     | 19      | 9
            Rotation.CW_270 | 10    | 20     | 0       | 19
    }

    void 'an image is not rotated when there is no rotation'() {

        given: 'an image'
            var image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB)

        expect: 'the same image to be returned'
            imageProcessor.rotate(image, Rotation.NONE).is(image)
    }
}
