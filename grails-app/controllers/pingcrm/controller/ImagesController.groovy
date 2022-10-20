package pingcrm.controller


import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.Validateable
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jakarta.inject.Inject
import pingcrm.FileService
import pingcrm.image.ImageProcessor

import static org.springframework.http.HttpStatus.*

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@Slf4j
@CompileStatic
@Secured('IS_AUTHENTICATED_REMEMBERED')
class ImagesController {

    private final FileService fileService

    final static defaultAction = 'thumbnail'

    @Inject
    ImagesController(FileService fileService) {
        this.fileService = fileService
    }

    def thumbnail(ImageThumbnailCommand cmd) {

        if(!cmd.validate()) { render status: UNPROCESSABLE_ENTITY; return }

        try {

            def image= fileService.createImageThumbnail cmd.path, [width: cmd.w, height: cmd.h, fit: cmd.fit, quality: cmd.quality]

            def contentType = 'image/jpg'
            def formatName = 'JPG'

            if(image.type == BufferedImage.TYPE_INT_ARGB) {
                contentType = 'image/png'
                formatName = 'PNG'
            }

            response.contentType = contentType
            ImageIO.write image, formatName, response.outputStream

        }
        catch(FileNotFoundException ignore) { render status: NOT_FOUND }
        catch(Exception ignore) { render status: INTERNAL_SERVER_ERROR }
    }
}

@GrailsCompileStatic
class ImageThumbnailCommand implements Validateable {

    String path, fit, quality
    Integer w,  h

    @SuppressWarnings('unused')
    static constraints = {
        w min: 20, max: 480
        h min: 20, max: 480
        fit nullable: true, inList: ImageProcessor.ResizingMode.validValues
        quality nullable: true, inList: ImageProcessor.ScalingQuality.validValues
    }
}
