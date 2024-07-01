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
package pingcrm

import grails.util.TypeConvertingMap
import grails.validation.Validateable
import grails.web.mapping.LinkGenerator
import groovy.transform.CompileStatic
import org.springframework.context.MessageSource

import javax.inject.Inject
import java.math.RoundingMode

/**
 * A class that provides pagination support.
 *
 * @author Mattias Reichel
 * @since 1.0.0
 */
@CompileStatic
class Paginator implements Validateable {

    @Inject LinkGenerator linkGenerator
    @Inject MessageSource messageSource

    int max
    int offset

    static int defaultMax = 10
    static int fetchMax = 100
    static int defaultOffset = 0

    int getMaxOrDefault() { Math.min(max ?: defaultMax, fetchMax) }
    int getOffsetOrDefault() { offset ?: defaultOffset }

    Map getQueryParams() { [max: maxOrDefault, offset: offsetOrDefault] }
    Map queryParamsAnd(Map extra) { queryParams + extra }


    /**
     * Creates links to support pagination.
     *
     * Possible paginationData properties include:
     *
     * <ul>
     *    <li>total REQUIRED - The total number of results to paginate</li>
     *    <li>action - the name of the action to use in the link, if not specified the default action will be linked</li>
     *    <li>controller - the name of the controller to use in the link, if not specified the current controller will be linked</li>
     *    <li>id - The id to use in the link</li>
     *    <li>params - A map containing request parameters to add to links</li>
     *    <li>prev - The text to display for the previous link (defaults to "Previous" as defined by default.paginate.prev property in I18n messages.properties)</li>
     *    <li>next - The text to display for the next link (defaults to "Next" as defined by default.paginate.next property in I18n messages.properties)</li>
     *    <li>omitPrev - Whether to not show the previous link (if set to true, the previous link will not be shown)</li>
     *    <li>omitNext - Whether to not show the next link (if set to true, the next link will not be shown)</li>
     *    <li>omitFirst - Whether to not show the first link (if set to true, the first link will not be shown)</li>
     *    <li>omitLast - Whether to not show the last link (if set to true, the last link will not be shown)</li>
     *    <li>max - The number of records displayed per page (defaults to 10). Used ONLY if params.max is empty</li>
     *    <li>maxSteps - The number of steps displayed for pagination (defaults to 10). Used ONLY if params.maxSteps is empty</li>
     *    <li>offset - Used only if params.offset is empty</li>
     *    <li>mapping - The named URL mapping to use to rewrite the link</li>
     *    <li>fragment - The link fragment (often called anchor tag) to use</li>
     * </ul>
     *
     * @param data = the list of items to return with the pagination.
     * @param currentRequestParams optional request parameters to be used
     * @param paginationData a map with settings for pagination
     * @return a map with data for pagination
     */
    Map paginate(List data, Map currentRequestParams, Map paginationData = [:]) {

        def pgData = new TypeConvertingMap(paginationData)
        def params = currentRequestParams instanceof TypeConvertingMap ? currentRequestParams : new TypeConvertingMap(currentRequestParams)

        if (pgData.total == null) { throw new IllegalArgumentException('paginationData is missing required entry [total]') }

        def locale = paginationData.locale as Locale ?: Locale.ENGLISH

        def total = pgData.int('total', 0)
        def offset = pgData.int('offset', offsetOrDefault)
        def maxItemsPerPage = params.int('max', maxOrDefault)
        def maxSteps = pgData.int('maxSteps', 10)
        if (!maxItemsPerPage) maxItemsPerPage = pgData.int('max', 10)

        Map linkParams = [:]
        linkParams.putAll(params)
        if (pgData.params instanceof Map) linkParams.putAll((Map)pgData.params)
        linkParams.offset = offset - maxItemsPerPage
        linkParams.max = maxItemsPerPage
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order

        // determine paging variables
        def stepsEnabled = maxSteps > 0
        int currentStep = ((offset / maxItemsPerPage) as int) + 1
        int firstStep = 1
        int lastStep = (total / maxItemsPerPage).setScale(0, RoundingMode.CEILING).intValue()

        def links = []

        // display previous link when not on first step unless omitPrev is true
        if (!pgData.boolean('omitPrev')) {
            def label = (pgData.prev ?: messageSource.getMessage('paginate.prev', null, messageSource.getMessage('default.paginate.prev', null, 'Previous', locale), locale))
            linkParams.offset = offset - maxItemsPerPage
            links << [active: false, label: label, url: (currentStep > firstStep) ? linkGenerator.link(params: linkParams): null]
        }

        // display steps when steps are enabled
        if (stepsEnabled && lastStep > firstStep) {

            // determine begin and end step paging variables
            int beginStep = currentStep - (maxSteps / 2).round().intValue() + (maxSteps % 2)
            int endStep = currentStep + (maxSteps / 2).round().intValue()

            if (beginStep < firstStep) {
                beginStep = firstStep
                endStep = maxSteps
            }
            if (endStep > lastStep) {
                beginStep = lastStep - maxSteps + 1
                if (beginStep < firstStep) {
                    beginStep = firstStep
                }
                endStep = lastStep
            }

            // display first step link when begin step is not first step
            if (beginStep > firstStep && !pgData.boolean('omitFirst')) {
                linkParams.offset = 0
                links << [active: false, label: firstStep.toString(), url: linkGenerator.link(params: linkParams)]
            }
            //show a gap if begin step isn't immediately after first step, and if were not omitting first or prev
            def gapLabel = (pgData.gap ?: messageSource.getMessage('paginate.gap', null, messageSource.getMessage('default.paginate.gap', null, '..', locale), locale))
            if (beginStep > firstStep+1 && (!pgData.boolean('omitFirst') || !pgData.boolean('omitPrev')) ) {
                links << [active: false, label: gapLabel, url: null]
            }

            // display paginate steps
            (beginStep..endStep).each { int i ->
                linkParams.offset = (i - 1) * maxItemsPerPage
                links << [active: currentStep == i, label: "$i", url: linkGenerator.link(params: linkParams)]
            }

            //show a gap if begin step isn't immediately before first step, and if were not omitting first or rev
            if (endStep+1 < lastStep && (!pgData.boolean('omitLast') || !pgData.boolean('omitNext'))) {
                links << [active: false, label: gapLabel, url: null]
            }
            // display last step link when end step is not last step
            if (endStep < lastStep && !pgData.boolean('omitLast')) {
                linkParams.offset = (lastStep - 1) * maxItemsPerPage
                links << [active: false, label: lastStep.toString(), url: linkGenerator.link(params: linkParams)]
            }
        }

        // display next link when not on last step unless omit next is true
        if (!pgData.boolean('omitNext')) {
            linkParams.offset = offset + maxItemsPerPage
            def nextLabel = pgData.next ? pgData.next : messageSource.getMessage('paginate.next', null, messageSource.getMessage('default.paginate.next', null, 'Next', locale), locale)
            links << [active: false, label: nextLabel, url: (currentStep < lastStep) ? linkGenerator.link(params: linkParams) : null]
        }

        return [
                current_page: currentStep,
                data: data,
                links: links,
                total: total
        ]
    }
}