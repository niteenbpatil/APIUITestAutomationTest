package utils;

import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ThreadGuard;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class DriverFactory {

    public static WebDriver driver;
    public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
    public static ThreadLocal<RemoteWebDriver> rtlDriver = new ThreadLocal<>();
    public static String browser = ReadConfigFile.readConfig("browser");
    public static String webSiteUrl = ReadConfigFile.readConfig("URL");
    public static String environmentType = System.getProperty("environmentType");
    public static DesiredCapabilities cap;


    Logger log = LogManager.getLogger(this.getClass().getName());

    public WebDriver createLocalDriver() {
        try {
            switch (browser.toLowerCase()) {

                case "chrome":
                    log.info("Chrome browser is getting instantiated..");
                    WebDriverManager.chromedriver().setup();
                    //tlDriver.set(new ChromeDriver());
                    tlDriver.set(ThreadGuard.protect(new ChromeDriver()));
                    //options - to set Capabilities for the chrome instance.
                    //ChromeOptions options = new ChromeOptions();
                    //driver = new ChromeDriver(options);
                    break;
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    //DesiredCapabilities capabilities = DesiredCapabilities.firefox();
                    //capabilities.setCapability("marionette", true);
                    tlDriver.set(ThreadGuard.protect(new FirefoxDriver()));
                    break;
                case "ie":
                    WebDriverManager.iedriver().setup();
                    tlDriver.set(ThreadGuard.protect(new InternetExplorerDriver()));
                    break;
                default:
                    log.info("Please pass the correct browser value: " + browser);
            }
            tlDriver.get().manage().deleteAllCookies();
            tlDriver.get().manage().window().maximize();
            tlDriver.get().manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
            tlDriver.get().get(webSiteUrl);
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tlDriver.get();
    }

    public WebDriver createRemoteDriver() throws MalformedURLException {
        try {
            switch (browser.toLowerCase()) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    cap = new DesiredCapabilities();
                    cap.setCapability(CapabilityType.BROWSER_NAME, "chrome");
                    break;
                case "firefox":
                    WebDriverManager.chromedriver().setup();
                    cap = new DesiredCapabilities();
                    cap.setCapability(CapabilityType.BROWSER_NAME, "firefox");
                    break;
                case "ie":
                    WebDriverManager.iedriver().setup();
                    cap = new DesiredCapabilities();
                    cap.setCapability(CapabilityType.BROWSER_NAME, "ie");
                    break;
            }
            rtlDriver.set(new RemoteWebDriver(new URL("http://192.168.0.12:4444/wd/hub"), cap));
            rtlDriver.get().manage().deleteAllCookies();
            rtlDriver.get().manage().window().maximize();
            rtlDriver.get().manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
            rtlDriver.get().get(webSiteUrl);
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtlDriver.get();
    }

    public static synchronized WebDriver getDriver() {

        if (environmentType.toLowerCase().equalsIgnoreCase("local")) {
            return tlDriver.get();
        } else if (environmentType.toLowerCase().equalsIgnoreCase("remote")) {
            return rtlDriver.get();
        } else
            return null;
    }

    public static void tearDownDrivers() {

        if (environmentType.toLowerCase().equalsIgnoreCase("local")) {
            if (tlDriver.get() != null) {
                tlDriver.get().quit();
                tlDriver.remove();
            }
        } else if (environmentType.toLowerCase().equalsIgnoreCase("remote")) {
            if (rtlDriver.get() != null) {
                rtlDriver.get().quit();
                rtlDriver.remove();
            }
        }

    }
}