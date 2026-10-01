package pingcrm.pages

import geb.Page
import geb.url.UrlFragment

abstract class BasePage extends Page implements LocalizedPage {

    // To be overridden by subclasses (see LocalizedPage)
    static heading = null

    static at = {

        // delegate here is the original page _instance_ (i.e. the subclass)
        var h1 = delegate.class.heading
        if (h1 == null) throw new IllegalStateException("No heading defined for page ${delegate.class.name}")
        delegate.expectedHeading == js.exec('return document.querySelector("h1").textContent')
        menuItemIsSelected // Without this I have gotten StaleElementReferenceException
    }

    static content = {
        navbarName {
            $('button[type=button].mt-1', 0)
                    .text()
        }

        userMenu {
            $('button[type=button].mt-1', 0)
        }

        logoutBtn(wait: true) {
            $('button', text: 'Logout')
        }

        menuItem { String href ->
            $("a[href='$href'].flex.items-center.group.py-3")
        }

        menuItemIsSelected {
            $("a[href='$url'].flex.items-center.group.py-3")
                    .find('div', 0)
                    .hasClass('text-white')
        }

        flashError(required: false) {
            $('div.bg-red-400')
                    .text()
        }

        flashSuccess(required: false) {
            $('div.bg-green-500')
                    .text()
        }
    }

    @Override
    void to(Map params, UrlFragment fragment, Object[] args) {
        super.to(withLangParam(params), fragment, args)
    }

    /**
     * Logs out with the logout button in the user menu (an Inertia DELETE request).
     */
    void logout() {
        userMenu.click()
        logoutBtn.click()
    }
}
