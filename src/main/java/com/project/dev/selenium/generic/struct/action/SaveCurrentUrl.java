/*
 * @fileoverview    {SaveCurrentUrl}
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
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
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
@ToString(callSuper = true)
public class SaveCurrentUrl extends Action {

    /**
     * Ejecuta una acción en el elemento de la página actual.
     *
     * @param driver  es el driver del navegador.
     * @param element es el {@code WebElement} que se le va a ejecutar dicha acción.
     * @return {@code true} si se ejecuta la acción correctamente.
     * @throws Exception si ocurre algún error ejecutando la acción indicada.
     */
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

}