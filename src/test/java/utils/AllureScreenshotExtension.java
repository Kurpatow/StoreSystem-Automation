package utils;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

public class AllureScreenshotExtension
        implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {

        if (context.getExecutionException().isPresent()) {

            byte[] screenshot =
                    Selenide.screenshot(OutputType.BYTES);

            if (screenshot != null) {
                Allure.addAttachment(
                        "Screenshot",
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        ".png"
                );
            }
        }
    }
}