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
package pingcrm.services

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jakarta.inject.Singleton
import org.springframework.web.multipart.MultipartFile
import pingcrm.image.ImageService

import java.awt.image.BufferedImage

/**
 * A service for handling files in the application.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@Slf4j
@Singleton
@CompileStatic
class FileService {

    private final ImageService imageService

    FileService(ImageService imageService) {
        this.imageService = imageService
    }

    BufferedImage createImageThumbnail(String path, Map options) throws FileNotFoundException, IOException {

        def file = new File(System.getProperty('java.io.tmpdir'), path)
        if(!file) throw new FileNotFoundException(path)

        def image
        try { image = imageService.resizeImage file, options }
        catch(Exception e) {
            log.info 'Image handling exception: {}', e.message
            throw e
        }
        image
    }

    static String uploadFile(MultipartFile uploadedFile, String namePrefix, String storagePath) {

        if(uploadedFile.isEmpty()) return null

        def storage = getFileStorage storagePath, true
        if(!storage) return null

        String fileExt = getSupportedFileExtension uploadedFile.contentType
        if(!fileExt) {
            log.debug 'File with contentType {} not accepted.', uploadedFile.contentType
            return null
        }

        try {
            File f = File.createTempFile namePrefix, fileExt, storage
            try {
                uploadedFile.transferTo f
                return f.name
            }
            catch(Exception fileTransferException) {
                log.error "Failed to save uploaded file as $f.absolutePath", fileTransferException
            }
        } catch(IOException tmpFileException) {
            log.error "Failed to create tmp file in $storage.absolutePath", tmpFileException
        }
        null
    }

    static boolean deleteFile(String filename, String storagePath) {
        def storage = getFileStorage storagePath
        def f = new File(storage, filename)
        f.delete()
    }

    static File getFileStorage(String storagePath, boolean create = false) {
        def storage = new File(storagePath)
        if(create && !storage.exists()) {
            log.info 'Creating storage dir: {}', storage.absolutePath
            if(!storage.mkdirs()) {
                log.error 'Failed to create storage dir: {}', storage.absolutePath
                return null
            }
        }
        storage
    }

    static String getSupportedFileExtension(String contentType) {
        String ext = null
        switch(contentType) {
            case 'image/png': ext = '.png'; break
            case 'image/gif': ext = '.gif'; break
            case 'image/jpg': ext = '.jpg'; break
            case 'image/jpeg': ext = '.jpg'; break
        }
        ext
    }
}
