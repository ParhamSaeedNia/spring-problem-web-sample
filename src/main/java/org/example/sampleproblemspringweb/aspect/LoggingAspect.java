package org.example.sampleproblemspringweb.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.sampleproblemspringweb.annotation.Loggable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * AOP Aspect for automatic method logging.
 * Logs method entry, exit, parameters, return values, execution time, and exceptions.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Pointcut for all methods in controllers
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {
    }

    /**
     * Pointcut for all methods in services
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {
    }

    /**
     * Pointcut for methods annotated with @Loggable
     */
    @Pointcut("@annotation(org.example.sampleproblemspringweb.annotation.Loggable)")
    public void loggableMethods() {
    }

    /**
     * Combined pointcut for controllers and services
     */
    @Pointcut("controllerMethods() || serviceMethods()")
    public void applicationMethods() {
    }

    /**
     * Around advice that logs method execution
     */
    @Around("applicationMethods() || loggableMethods()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        String fullMethodName = className + "." + methodName;

        // Get @Loggable annotation if present
        Loggable loggable = method.getAnnotation(Loggable.class);
        if (loggable == null) {
            loggable = joinPoint.getTarget().getClass().getAnnotation(Loggable.class);
        }

        // Determine log level
        Loggable.LogLevel logLevel = loggable != null ? loggable.value() : Loggable.LogLevel.INFO;
        boolean logParams = loggable == null || loggable.logParams();
        boolean logResult = loggable == null || loggable.logResult();
        boolean logExecutionTime = loggable == null || loggable.logExecutionTime();

        // Log method entry
        if (logParams) {
            Object[] args = joinPoint.getArgs();
            String params = formatArguments(args);
            logMessage(logLevel, "→ Entering method: {} with parameters: {}", fullMethodName, params);
        } else {
            logMessage(logLevel, "→ Entering method: {}", fullMethodName);
        }

        StopWatch stopWatch = new StopWatch();
        Object result = null;

        try {
            stopWatch.start();
            result = joinPoint.proceed();
            stopWatch.stop();

            // Log method exit
            if (logExecutionTime) {
                long executionTime = stopWatch.getTotalTimeMillis();
                if (logResult && result != null) {
                    String resultStr = formatReturnValue(result);
                    logMessage(logLevel, "← Exiting method: {} | Execution time: {}ms | Return: {}", 
                        fullMethodName, executionTime, resultStr);
                } else {
                    logMessage(logLevel, "← Exiting method: {} | Execution time: {}ms", 
                        fullMethodName, executionTime);
                }
            } else {
                if (logResult && result != null) {
                    String resultStr = formatReturnValue(result);
                    logMessage(logLevel, "← Exiting method: {} | Return: {}", fullMethodName, resultStr);
                } else {
                    logMessage(logLevel, "← Exiting method: {}", fullMethodName);
                }
            }

            return result;

        } catch (Throwable e) {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();

            // Log exception
            logger.error("✗ Exception in method: {} | Execution time: {}ms | Exception: {} - {}", 
                fullMethodName, executionTime, e.getClass().getSimpleName(), e.getMessage(), e);

            throw e;
        }
    }

    /**
     * Format method arguments for logging
     */
    private String formatArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        try {
            return Arrays.toString(args);
        } catch (Exception e) {
            return "[Unable to serialize arguments]";
        }
    }

    /**
     * Format return value for logging
     */
    private String formatReturnValue(Object result) {
        if (result == null) {
            return "null";
        }

        try {
            // If it's a simple type or small object, return as string
            if (result instanceof String || result instanceof Number || result instanceof Boolean) {
                return result.toString();
            }

            // For collections, show size
            if (result instanceof java.util.Collection) {
                java.util.Collection<?> collection = (java.util.Collection<?>) result;
                return String.format("%s[size=%d]", result.getClass().getSimpleName(), collection.size());
            }

            // For arrays, show length
            if (result.getClass().isArray()) {
                return String.format("%s[length=%d]", result.getClass().getSimpleName(), 
                    java.lang.reflect.Array.getLength(result));
            }

            // Try to serialize as JSON for complex objects
            try {
                return objectMapper.writeValueAsString(result);
            } catch (Exception e) {
                return String.format("%s[toString=%s]", result.getClass().getSimpleName(), result.toString());
            }
        } catch (Exception e) {
            return result.getClass().getSimpleName() + "[Unable to serialize]";
        }
    }

    /**
     * Log message with appropriate log level
     */
    private void logMessage(Loggable.LogLevel level, String message, Object... args) {
        switch (level) {
            case DEBUG:
                logger.debug(message, args);
                break;
            case INFO:
                logger.info(message, args);
                break;
            case WARN:
                logger.warn(message, args);
                break;
            case ERROR:
                logger.error(message, args);
                break;
            default:
                logger.info(message, args);
        }
    }
}

