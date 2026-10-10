package config;

import com.codeborne.selenide.Configuration;

public class SelenideConfig {

    public static void setup() {
        Configuration.remote = System.getProperty(
                "selenide.remote",
                "http://localhost:4444/wd/hub"
        );
        Configuration.browser = "chrome";
        Configuration.browserSize = "1366x768";
        Configuration.timeout = 5000;
    }
}
