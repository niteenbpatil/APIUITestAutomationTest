package parallel;

import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        plugin = {"pretty",
                "rerun:target/failedrerun.txt"
        },
        monochrome = true,
        glue = {"parallel"},
        features = {"@target/failedrerun.txt"}
)


public class TestNGFailedRerun extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}