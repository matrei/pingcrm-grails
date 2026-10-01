package pingcrm.services

import org.springframework.mock.web.MockMultipartFile
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification

import pingcrm.config.UploadConfig

class PhotoUploadValidatorSpec extends Specification {

    private static final long MAX_FILE_SIZE = 512 * 1024

    private final PhotoUploadValidator validator = new PhotoUploadValidator(
            new UploadConfig(maxFileSize: MAX_FILE_SIZE)
    )

    void 'a file at the configured limit is accepted'() {
        given:
            var file = fileOfSize(MAX_FILE_SIZE, 'image/jpeg')
            var errors = errors()

        expect:
            validator.validate(file, errors)
            !errors.hasErrors()
    }

    void 'a file over the configured limit is rejected'() {
        given:
            var file = fileOfSize(MAX_FILE_SIZE + 1, 'image/jpeg')
            var errors = errors()

        expect:
            !validator.validate(file, errors)
            errors.hasFieldErrors('photo')
            errors.getFieldError('photo').code == 'user.photo.max.size.exceeded'
    }

    void 'an unsupported content type is rejected'() {
        given:
            var file = fileOfSize(1, 'application/pdf')
            var errors = errors()

        expect:
            !validator.validate(file, errors)
            errors.getFieldError('photo').code == 'user.photo.invalid.type'
    }

    void 'no file is accepted'() {
        expect:
            validator.validate(null, errors())
    }

    private static MockMultipartFile fileOfSize(long size, String contentType) {
        new MockMultipartFile('photo', 'photo', contentType, new byte[size as int])
    }

    private static BeanPropertyBindingResult errors() {
        new BeanPropertyBindingResult(new ValidationTarget(), 'user')
    }

    private static class ValidationTarget {
        MultipartFile photo
    }
}
