/*
 * @overview        {Application}
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
package com.project.dev;

import com.project.dev.selenium.generic.processor.SeleniumProcessor;

/**
 * TODO: Description of {@code Application}.
 *
 * @author Dyson Parra
 * @since Java 17 (LTS), Gradle 7.3
 */
public class Application {

    /**
     * Entrada principal del sistema.
     *
     * @param args argumentos pasados por consola.
     */
    public static void main(String[] args) {
        SeleniumProcessor.run(args);
    }

}
