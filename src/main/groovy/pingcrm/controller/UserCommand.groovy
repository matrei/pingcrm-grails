package pingcrm.controller

import groovy.util.logging.Slf4j
import pingcrm.User
import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import org.apache.commons.io.FileUtils
import org.springframework.validation.Errors
import org.springframework.web.multipart.MultipartFile
import pingcrm.FileService
import pingcrm.UserService

@Slf4j
@GrailsCompileStatic
class UserCommand implements Validateable {

    // TODO: fetch photo storage path from config
    private final String photoStorage = "ping-crm-users"

    Long id
    String firstName
    String lastName
    String email
    String password
    Boolean owner
    MultipartFile photo
    Long clientSaysPhotoTooLarge
    Long serverSaysPhotoTooLarge

    private String photoFilename
    String createPhotoPath() { photoFilename ? "$photoStorage${File.separator}$photoFilename" : null }

    static constraints = {
        id nullable: true
        email email: true, validator: { String email, UserCommand cmd, Errors errors ->
            if(!UserService.isEmailUniqueIgnoreId(email, cmd.id)) {
                errors.rejectValue('email','user.email.not.unique', ['email', email] as Object[], 'The email has already been taken.')
                return true
            }
            false
        }
        photo nullable: true, validator: { MultipartFile file, UserCommand cmd, Errors errors ->
            if(cmd.clientSaysPhotoTooLarge || cmd.serverSaysPhotoTooLarge) {
                long limit = cmd.clientSaysPhotoTooLarge ?: cmd.serverSaysPhotoTooLarge
                errors.rejectValue('photo','user.photo.max.size.exceeded', [FileUtils.byteCountToDisplaySize(limit)] as Object[], 'The maximum file size of {0} was exceeded.')
                return true
            }
            if(file && !FileService.getSupportedFileExtension(file.contentType)) {
                errors.rejectValue('photo','user.photo.invalid.type', [file.contentType] as Object[], 'Invalid file type.')
                return true
            }
            false
        }
        serverSaysPhotoTooLarge nullable: true
        clientSaysPhotoTooLarge nullable: true
    }

    void transferValuesTo(User user) {
        user.firstName = firstName
        user.lastName = lastName
        user.owner = owner
        user.email = email.toLowerCase(Locale.ENGLISH)
        if(photo) user.photoPath = createPhotoPath()
        if(password) user.password = password
    }

    boolean storePhoto() {
        if(photo) {
            try {
                photoFilename = FileService.uploadFile photo, "user-$id-", "${System.getProperty('java.io.tmpdir')}${File.separator}$photoStorage"
                return true
            }
            catch(Exception e) {
                log.info('Could not store photo', e)
                return false
            }
        }
        false
    }

    boolean isOwner() { owner?.booleanValue() }

    // Used to ignore any tries to set non-existing properties when binding data
    def propertyMissing(String name, Object value) {}
}
