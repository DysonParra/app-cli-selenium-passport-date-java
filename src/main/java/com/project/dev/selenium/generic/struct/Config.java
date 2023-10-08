/*
 * @fileoverview    {Config} se encarga de realizar tareas especificas.
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
package com.project.dev.selenium.generic.struct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO: Definición de {@code Page}.
 *
 * @author Dyson Parra
 * @since 1.8
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Config {

    private String name;
    private Class type;
    private Object value;
    private Object defaultValue;

    /**
     * Get {@code value} if is not {@code null}, or {@code defaultValue} if it is.
     *
     * @return {@code value} or {@code defaultValue}
     */
    public Object getCanonicalValue() {
        return value != null ? value : defaultValue;
    }
}
