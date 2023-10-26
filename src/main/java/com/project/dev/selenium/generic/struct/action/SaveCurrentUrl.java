/*
 * @fileoverview    {SaveCurrentUrl} se encarga de realizar tareas especificas.
 *
 * @version         2.0
 *
 * @author          Dyson Arley Parra Tilano <dysontilano@gmail.com>
 *
 * @copyright       Dyson Parra
 * @see             github.com/DysonParra
 *
 * History
 * @version 1.0     Implementación realizada.
 * @version 2.0     Documentación agregada.
 */
package com.project.dev.selenium.generic.struct.action;

import com.project.dev.selenium.generic.struct.Action;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * TODO: Definición de {@code SaveCurrentUrl}.
 *
 * @author Dyson Parra
 * @since 1.8
 */
@AllArgsConstructor
@Builder
@Data
public class SaveCurrentUrl extends Action {

    @Override
    public boolean executeAction(@NonNull WebDriver driver, @NonNull WebElement element) throws Exception {
        //System.out.println("SaveCurrentUrl");
        System.out.println("Current page: " + driver.getCurrentUrl());
        try (FileOutputStream fos = new FileOutputStream(properties.get("-outputPath") + "\\" + value, true);
                OutputStreamWriter osr = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                BufferedWriter writer = new BufferedWriter(osr);) {
            writer.write(driver.getCurrentUrl() + "\n");

        } catch (Exception e) {
            System.out.println("Errow saving current url on a file");
        }
        return true;
    }

    @Override
    public String toString() {
        return "Action{" + "type=" + type + ", value=" + value + ", delay=" + delay + ", properties=" + properties + '}';
    }

}
