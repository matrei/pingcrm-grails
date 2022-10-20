package pingcrm

import grails.web.mapping.LinkGenerator
import org.springframework.context.MessageSource
import spock.lang.Specification

class PaginatorSpec extends Specification {

    void 'pagination output is correct'() {

        given: 'a paginator'
        def paginator = new Paginator(offset: offset)
        def linkGenerator = Stub(LinkGenerator)
        linkGenerator.link(_) >> 'a'
        paginator.linkGenerator = linkGenerator

        paginator.messageSource = Mock(MessageSource)

        when: 'paginating the items'
        def result = paginator.paginate([], [:], [
            total: total,
            max: maxItemsPerPage,
            omitPrev: omitPrev,
            omitNext: omitNext,
            maxSteps: maxSteps,
            prev: '<<', next: '>>', gap: '...'
        ])

        then: 'the pagination steps are as expected'
        result.links == steps

        where:
        offset | maxItemsPerPage | omitPrev | omitNext | maxSteps | total || steps
             0 |            null |    false |    false |     null |   100 || [[label: '<<', active: false, url: null], [label: '1', active: true, url: 'a'], [label: '2', active: false, url: 'a'], [label: '3', active: false, url: 'a'], [label: '4', active: false, url: 'a'], [label: '5', active: false, url: 'a'], [label: '6', active: false, url: 'a'], [label: '7', active: false, url: 'a'], [label: '8', active: false, url: 'a'], [label: '9', active: false, url: 'a'], [label: '10', active: false, url: 'a'], [label: '>>', active: false, url: 'a']]
            90 |            null |    false |    false |     null |   100 || [[label: '<<', active: false, url: 'a'], [label: '1', active: false, url: 'a'], [label: '2', active: false, url: 'a'], [label: '3', active: false, url: 'a'], [label: '4', active: false, url: 'a'], [label: '5', active: false, url: 'a'], [label: '6', active: false, url: 'a'], [label: '7', active: false, url: 'a'], [label: '8', active: false, url: 'a'], [label: '9', active: false, url: 'a'], [label: '10', active: true, url: 'a'], [label: '>>', active: false, url: null]]
             0 |            null |    false |    false |     null |    73 || [[label: '<<', active: false, url: null], [label: '1', active: true, url: 'a'], [label: '2', active: false, url: 'a'], [label: '3', active: false, url: 'a'], [label: '4', active: false, url: 'a'], [label: '5', active: false, url: 'a'], [label: '6', active: false, url: 'a'], [label: '7', active: false, url: 'a'], [label: '8', active: false, url: 'a'], [label: '>>', active: false, url: 'a']]
             0 |            null |    false |    false |     null |     3 || [[label: '<<', active: false, url: null], [label: '>>', active: false, url: null]]
             0 |            null |    false |    false |     null |   101 || [[label: '<<', active: false, url: null], [label: '1', active: true, url: 'a'], [label: '2', active: false, url: 'a'], [label: '3', active: false, url: 'a'], [label: '4', active: false, url: 'a'], [label: '5', active: false, url: 'a'], [label: '6', active: false, url: 'a'], [label: '7', active: false, url: 'a'], [label: '8', active: false, url: 'a'], [label: '9', active: false, url: 'a'], [label: '10', active: false, url: 'a'], [label: '11', active: false, url: 'a'], [label: '>>', active: false, url: 'a']]
    }
}
