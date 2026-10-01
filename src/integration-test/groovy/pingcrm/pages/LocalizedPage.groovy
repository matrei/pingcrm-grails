package pingcrm.pages

/**
 * A page that can be checked in different UI languages.
 *
 * The static {@code heading} of a page is either the heading in the default language,
 * or a map of headings by language, e.g. {@code [en: 'Dashboard', sv: 'Översikt']}.
 */
trait LocalizedPage {

    /**
     * The UI language of the page (the default language when not set).
     * When set, it is also requested with the {@code lang} parameter when navigating to the page.
     */
    String lang

    String getExpectedHeading() {
        var heading = this.class.heading
        heading instanceof Map ? heading[lang ?: 'en'] : heading
    }

    Map withLangParam(Map params) {
        lang ? [lang: lang] + params : params
    }
}
