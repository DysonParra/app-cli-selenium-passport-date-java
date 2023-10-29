/*
 * @fileoverview    {Action}
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
package com.project.dev.selenium.generic.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.json.simple.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * TODO: Definición de {@code Action}.
 *
 * @author Dyson Parra
 * @since 1.8
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public abstract class Action {

    protected String type;
    @JsonProperty(value = "delay-before-next")
    protected long delay;

    /**
     * Ejecuta una acción en el elemento de la página actual.
     *
     * @param driver  es el driver del navegador.
     * @param element es el {@code WebElement} que se le va a ejecutar dicha acción.
     * @param flagsMap contiene las {@code Flag} pasadas por consola.
     * @return {@code true} si se ejecuta la acción correctamente.
     * @throws Exception si ocurre algún error ejecutando la acción indicada.
     */
    public abstract boolean executeAction(@NonNull WebDriver driver, @NonNull WebElement element, Map<String, String> flagsMap) throws Exception;

}
