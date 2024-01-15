/*
* Copyright 2022-2023 original authors
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

/* https://stackoverflow.com/a/61083271 */

/**
 * Format file size in metric prefix
 * @param fileSize
 * @param fractionDigits
 * @returns {string}
 */
export function formatFileSizeMetric(fileSize, fractionDigits = 2) {
    let size = Math.abs(fileSize);

    if (Number.isNaN(size)) {
        return 'Invalid file size';
    }

    if (size === 0) {
        return '0 bytes';
    }

    const units = ['bytes', 'kB', 'MB', 'GB', 'TB'];
    let quotient = Math.floor(Math.log10(size) / 3);
    quotient = quotient < units.length ? quotient : units.length - 1;
    size /= (1000 ** quotient);

    return `${+size.toFixed(fractionDigits)} ${units[quotient]}`;
}

/**
 * Format file size in binary prefix
 * @param fileSize
 * @param fractionDigits
 * @returns {string}
 */
export function formatFileSizeBinary(fileSize, fractionDigits = 2) {
    let size = Math.abs(fileSize);

    if (Number.isNaN(size)) {
        return 'Invalid file size';
    }

    if (size === 0) {
        return '0 bytes';
    }

    const units = ['bytes', 'kiB', 'MiB', 'GiB', 'TiB'];
    let quotient = Math.floor(Math.log2(size) / 10);
    quotient = quotient < units.length ? quotient : units.length - 1;
    size /= (1024 ** quotient);

    return `${+size.toFixed(fractionDigits)} ${units[quotient]}`;
}