package pingcrm.pages

import geb.Page

abstract class BasePage extends Page {

    // To be overridden by subclasses
    static heading = null

    static at = {

        // delegate here is the original page _instance_ (i.e. the subclass)
        def h1 = delegate.class.heading
        if(h1 == null) throw new IllegalStateException("No heading defined for page ${delegate.class.name}")

        waitFor {
            h1 == js.exec('return document.querySelector("h1").textContent')
            menuItemIsSelected // Without this I have gotten StaleElementReferenceException
        }
    }

    static content = {
        navbarName { $('button[type=button].mt-1', 0).text() }
        menuItemIsSelected { $("a[href='$url'].flex.items-center.group.py-3").find('div', 0).hasClass('text-white') }
        flashError(required: false) { $('div.bg-red-400').text() }
        flashSuccess(required: false) { $('div.bg-green-500').text() }
    }
}
