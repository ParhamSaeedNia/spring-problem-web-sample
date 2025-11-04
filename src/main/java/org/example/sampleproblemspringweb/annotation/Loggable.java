package org.example.sampleproblemspringweb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable AOP logging for methods.
 * When applied to a method, it will automatically log:
 * - Method entry with parameters
 * - Method exit with return value
 * - Execution time
 * - Exceptions
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    /**
     * Log level for entry/exit logs
     */
    LogLevel value() default LogLevel.INFO;

    /**
     * Whether to log method parameters
     */
    boolean logParams() default true;

    /**
     * Whether to log return value
     */
    boolean logResult() default true;

    /**
     * Whether to log execution time
     */
    boolean logExecutionTime() default true;

    enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}

