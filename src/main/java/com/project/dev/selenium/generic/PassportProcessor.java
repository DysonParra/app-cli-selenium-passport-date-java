/*
 * @fileoverview    {PassportProcessor}
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
package com.project.dev.selenium.generic;

import com.project.dev.flag.processor.Flag;
import com.project.dev.flag.processor.FlagProcessor;

/**
 * TODO: Definición de {@code Application}.
 *
 * @author Dyson Parra
 * @since 1.8
 */
public class PassportProcessor {

    /**
     * Procesa las flags pasadas por consola y ejecuta la navegación indicada en los archivos de
     * configuración.
     *
     * @param args argumentos de la linea de comandos.
     */
    public static void run(String[] args) {
        System.out.println("\n...START...");

        String requiredFlags[][] = {
            {"-chromeDriverPath"},
            {"-navigationFilePath"},
            {"-dataFilePath"},
            {"-outputPath"}
        };

        String optionalFlags[][] = {
            {"-chromeProfileDir"},
            {"--notUseIncognito"},
            {"-chromeUserDataDir"},};

        String defaultArgs[] = {
            "-chromeDriverPath",
            "res\\chromedriver.exe",
            "-navigationFilePath",
            "res\\navigation.json",
            "-dataFilePath",
            "res\\data.json",
            "-outputPath",
            "res\\output",
            "-outputFile",
            "output.log",
            "-chromeProfileDir",
            "Profile 1",
            "--notUseIncognito",};

        // for (String arg : args)
        //     System.out.println(arg);
        Flag[] flags;
        flags = FlagProcessor.convertArgsToFlags(args, defaultArgs, requiredFlags, optionalFlags, true);
        if (flags == null) {
            System.out.println("...ERROR IN FLAGS...");
            return;
        }

        FlagProcessor.printFlagsArray(flags, true);

        boolean result;
        result = ActionProcessor.processFlags(flags);
        System.out.println("last result = " + result);
        System.out.println("...END...");
    }

}
