/*
 * @fileoverview    {WaitPageLoad}
 *
 * @version         2.0
 *
 * @author          Dyson Arley Parra Tilano <dysontilano@gmail.com>
 *
 * @copyright       Dyson Parra
 * @see             github.com/DysonParra
 *
 * History
 * @version 1.0     Implementation done.
 * @version 2.0     Documentation added.
 */
package com.project.dev.selenium.generic.struct.action;

import com.project.dev.selenium.generic.struct.Action;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * TODO: Definición de {@code WaitPageLoad}.
 *
 * @author Dyson Parra
 * @since 1.8
 */
@AllArgsConstructor
@Builder
@Data
public class WaitPageLoad extends Action {

    @Override
    public boolean executeAction(@NonNull WebDriver driver, @NonNull WebElement element) throws Exception {
        //System.out.println("WaitPageLoad");
        new WebDriverWait(driver, Duration.ofMillis(Integer.parseInt(value)))
                                    .until((WebDriver webDriver) -> ((JavascriptExecutor) webDriver)
                                    .executeScript("return document.readyState")
                                    .equals("complete"));
        //System.out.println("Page loaded.");
        return true;
    }

    @Override
    public String toString() {
        return "Action{" + "type=" + type + ", value=" + value + ", delay=" + delay + ", properties=" + properties + '}';
    }

}
