import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.RemoteWebDriver

driver = {
    // Pin the browser language, the UI language follows the Accept-Language header
    new RemoteWebDriver(new FirefoxOptions().addPreference('intl.accept_languages', 'en'))
}
containerBrowser = 'firefox'
