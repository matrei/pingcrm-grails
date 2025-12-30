import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.RemoteWebDriver

driver = {
    new RemoteWebDriver(new FirefoxOptions())
}
containerBrowser = 'firefox'