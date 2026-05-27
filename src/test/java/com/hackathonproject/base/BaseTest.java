package com.hackathonproject.base;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import com.hackathonproject.util.ConfigReader;

import java.io.File;
import java.time.Duration;

public class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    private static WebDriver driver;

    public static WebDriver getDriver() {
        return driver;
    }

    @Before
    public void setUp() {
        String browser = com.hackathonproject.runner.TestRunner.getBrowserName() != null
                ? com.hackathonproject.runner.TestRunner.getBrowserName()
                : ConfigReader.get("browser");

        log.info("Initializing Browser: {}", browser);

        if (browser.equalsIgnoreCase("chrome")) {
            String path = requireDriver(ConfigReader.get("chrome.driver.path"), "Chrome");
            System.setProperty("webdriver.chrome.driver", path);
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            driver = new ChromeDriver(options);

        } else if (browser.equalsIgnoreCase("edge")) {
            String path = requireDriver(ConfigReader.get("edge.driver.path"), "Edge");
            System.setProperty("webdriver.edge.driver", path);
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--start-maximized");
            driver = new EdgeDriver(options);

        } else if (browser.equalsIgnoreCase("firefox")) {
            String path = requireDriver(ConfigReader.get("firefox.driver.path"), "Firefox");
            System.setProperty("webdriver.gecko.driver", path);
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--start-maximized");
            driver = new FirefoxDriver(options);

        } else {
            log.error("Browser not supported: {}", browser);
            throw new IllegalArgumentException("Browser not supported: " + browser);
        }

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getInt("implicit.wait")));
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigReader.getInt("page.load.timeout")));

        driver.get(ConfigReader.get("base.url"));
        log.info("Navigated to Practo home page");
    }

    @After
    public void tearDown() {
        if (driver != null) {
            log.info("Closing browser after scenario");
            driver.quit();
            driver = null;
        }
    }

    private String requireDriver(String path, String browserLabel) {
        if (path == null || path.isBlank()) {
            throw new IllegalStateException(
                    browserLabel + " driver path is not configured. Check config.properties.");
        }
        File f = new File(path);
        if (!f.exists()) {
            throw new IllegalStateException(
                    browserLabel + " driver not found at: " + f.getAbsolutePath()
                            + "\nPlace the executable in src/test/resources/drivers/ "
                            + "and update config.properties.");
        }
        return path;
    }
}