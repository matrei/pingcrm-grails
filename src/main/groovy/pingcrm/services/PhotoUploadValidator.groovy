/*
 * Copyright 2026-present original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package pingcrm.services

import groovy.transform.CompileStatic

import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Service
import org.springframework.validation.Errors
import org.springframework.web.multipart.MultipartFile

import pingcrm.config.UploadConfig

@CompileStatic
@Service
class PhotoUploadValidator {

    private final UploadConfig uploadConfig

    PhotoUploadValidator(UploadConfig uploadConfig) {
        this.uploadConfig = uploadConfig
    }

    boolean validate(MultipartFile file, Errors errors) {
        if (!file) return true

        if (file.size > uploadConfig.maxFileSize) {
            errors.rejectValue('photo', 'user.photo.max.size.exceeded',
                    [FileUtils.byteCountToDisplaySize(uploadConfig.maxFileSize)] as Object[],
                    'The maximum file size of {0} was exceeded.')
            return false
        }

        if (!FileService.getSupportedFileExtension(file.contentType)) {
            errors.rejectValue('photo', 'user.photo.invalid.type', [file.contentType] as Object[],
                    'Invalid file type.')
            return false
        }

        return true
    }
}
