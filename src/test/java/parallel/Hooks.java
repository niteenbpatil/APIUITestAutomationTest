package parallel;

import ApiUtils.RestAssuredExtension;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assume;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import utils.DriverFactory;

import java.net.MalformedURLException;

public class Hooks extends DriverFactory {
    Logger log = LogManager.getLogger(this.getClass().getName());
    private String environmentType = System.getProperty("environmentType");

    @Before(value = "@Skip", order = 0)
    public void skipScenario(Scenario scenario) {
        System.out.println("Skipping scenario: " + scenario.getName());
        Assume.assumeTrue(false);
    }

    @Before(value = "@FrontEnd", order = 1)
    public void setup() throws MalformedURLException {

        switch (environmentType.toLowerCase()) {
            case "local":
                log.info("Initiating the Local WebDriver instance...");
                createLocalDriver();
                break;
            case "remote":
                log.info("Initiating the Remote WebDriver instance...");
                createRemoteDriver();
                break;
            default:
                log.error("Invalid environmentType passed it should be with LOCAL or REMOTE");
        }
    }

    @Before(value = "@BackEnd", order = 2)
    public void TestSetup() {
        RestAssuredExtension RestAssuredExtension = new RestAssuredExtension();
    }

    @After(order = 1)
    public void addScreenshot(Scenario scenario) {
        

        if (scenario.isFailed()) {
            String screenshotName = scenario.getName().replaceAll(" ", "_");
            //validate if scenario has failed
            byte[] sourcePath = new byte[0];
            if (environmentType.toLowerCase().equalsIgnoreCase("local")) {
                sourcePath = ((TakesScreenshot) tlDriver.get()).getScreenshotAs(OutputType.BYTES);
                
            } else if (environmentType.toLowerCase().equalsIgnoreCase("remote")) {
                sourcePath = ((TakesScreenshot) rtlDriver.get()).getScreenshotAs(OutputType.BYTES);
            }
            scenario.attach(sourcePath, "image/png", screenshotName);
        }
    }

    @After(value = "@FrontEnd", order = 0)
    public void tearDown() {
        tearDownDrivers();
    }

}