package pingcrm.services

import java.nio.file.Files

import spock.lang.Specification

import pingcrm.image.ImageService

class FileServiceSpec extends Specification {

    private final ImageService imageService = Mock()
    private final FileService fileService = new FileService(imageService)

    void 'a thumbnail of a file that does not exist is not found'() {

        when:
            fileService.createImageThumbnail("does-not-exist-${UUID.randomUUID()}.jpg", [:])

        then: 'the image is not read'
            thrown(FileNotFoundException)
            0 * imageService.resizeImage(_, _)
    }

    void 'a thumbnail of a directory is not found'() {

        given:
            var tmpDir = new File(System.getProperty('java.io.tmpdir'))
            var directory = Files.createTempDirectory(tmpDir.toPath(), 'file-service-spec').toFile()

        when:
            fileService.createImageThumbnail(directory.name, [:])

        then: 'the image is not read'
            thrown(FileNotFoundException)
            0 * imageService.resizeImage(_, _)

        cleanup:
            directory.delete()
    }
}
