/*
 * @fileoverview    {SaveCurrentPage}
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.dev.selenium.generic.SeleniumProcessor;
import com.project.dev.selenium.generic.struct.Action;
import java.io.File;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * TODO: Definición de {@code SaveCurrentPage}.
 *
 * @author Dyson Parra
 * @since 1.8
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class SaveCurrentPage extends Action {

    @JsonProperty(value = "value")
    protected String value;

    /**
     * Ejecuta una acción en el elemento de la página actual.
     *
     * @param driver  es el driver del navegador.
     * @param element es el {@code WebElement} que se le va a ejecutar dicha acción.
     * @param flagsMap contiene las {@code Flag} pasadas por consola.
     * @return {@code true} si se ejecuta la acción correctamente.
     * @throws Exception si ocurre algún error ejecutando la acción indicada.
     */
    @Override
    public boolean executeAction(@NonNull WebDriver driver, @NonNull WebElement element, Map<String, String> flagsMap) throws Exception {
        //System.out.println("SaveCurrentPage");
        String outputPath = flagsMap.get("-outputPath");
        if (value == null)
            SeleniumProcessor.getPageSource(driver, outputPath);
        else
            SeleniumProcessor.getPageSource(driver, new File(outputPath, value));
        return true;
    }

}
