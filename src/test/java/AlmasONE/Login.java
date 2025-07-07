package AlmasONE;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.swing.*;
import java.io.FileInputStream;
import java.time.Duration;
import java.util.Properties;
import java.util.Scanner;

public class Login {

    // initialize WebDriver and test data variables
    WebDriver driver;
    WebDriverWait wait;
    String baseURL = "https://insights.almasequities.com/";
    String testEmail = "tadamij492@benznoi.com"; // test email credential

    @BeforeTest
    public void BeforeTestMethod(){

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // test cases
    @Test
    public void UserLogin(){
        try{
            // navigate to almasONE login page
            driver.get(baseURL);
            System.out.println("Navigated to: " + baseURL);

            // enter email and click continue
            WebElement email = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"app-root\"]/div[1]/div[2]/div/div/div[2]/div/div[1]/form/label/input")));
            email.sendKeys(testEmail);
            WebElement continueBTN = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"app-root\"]/div[1]/div[2]/div/div/div[2]/div/div[2]/button")));
            continueBTN.click();

            System.out.println("Entered email and clicked Continue and waiting for enter OTP");

            // get OTP from user input
            String otp = getOTPFromUser();
            System.out.println("Using OTP: " + otp);

            // enter OTP
            WebElement OTP = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"app-root\"]/div[1]/div[2]/div/div/div[2]/div/div[1]/form/label/input")));
            OTP.sendKeys(otp);
            WebElement signInBTN = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"app-root\"]/div[1]/div[2]/div/div/div[2]/div/div[2]/button[1]")));
            signInBTN.click();

            System.out.println("Entered OTP and clicked Sign IN");

            // verify successful login
            WebElement dashboardHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"main-root\"]/div[2]/div[1]/div/div/div/div/div/div/h1")));
            Assert.assertTrue(dashboardHeader.isDisplayed(), "Login verification failed!");
            System.out.println("Login successful");

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            Assert.fail("Test execution failed: " + e.getMessage());
        }
    }

    // gets OTP using multiple fallback methods
    private String getOTPFromUser() {
        // first try reading from properties file
        String otp = tryPropertiesFile();
        if (otp != null) return otp;

        // try GUI input dialog if properties file fails
        otp = tryGuiInput();
        if (otp != null) return otp;

        // final fallback to console input
        return tryConsoleInput();
    }

    // attempts to read from config.properties file
    private String tryPropertiesFile() {
        try {
            Properties props = new Properties();
            // load properties file from project root
            props.load(new FileInputStream("config.properties"));
            String otp = props.getProperty("otp");
            if (otp != null && !otp.trim().isEmpty()) {
                System.out.println("Using OTP from properties file");
                return otp.trim();
            }
        } catch (Exception e) {
            System.out.println("Could not read OTP from properties file");
        }
        return null;
    }

    // attempts to get OTP via GUI input dialog
    private String tryGuiInput() {
        try {
            String otp = JOptionPane.showInputDialog(
                    null,
                    "Enter OTP sent to your email",
                    "OTP Required",
                    JOptionPane.QUESTION_MESSAGE
            );
            if (otp != null && !otp.trim().isEmpty()) {
                System.out.println("Using OTP from GUI input");
                return otp.trim();
            }
        } catch (Exception e) {
            System.out.println("GUI input not available");
        }
        return null;
    }

    // final fallback - gets OTP from console input
    private String tryConsoleInput() {
        System.out.println("Please enter the OTP from your email and press Enter:");
        return new Scanner(System.in).nextLine().trim();
    }


    @AfterTest
    public void TestAfterMethod(){
        if (driver != null){
            driver.quit();
            System.out.println("Browser closed successfully");
        }
    }
}
