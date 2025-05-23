package com.github.doquanghop.chat_app.infrastructure.aop;

import com.github.doquanghop.chat_app.infrastructure.annotation.logging.ActionLog;
import com.github.doquanghop.chat_app.infrastructure.annotation.logging.MaskSensitive;
import com.github.doquanghop.chat_app.infrastructure.helper.SensitiveDataMasker;
import com.github.doquanghop.chat_app.infrastructure.utils.LogUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging method execution details when annotated with @ActionLog.
 * Uses SensitiveDataMasker to mask sensitive fields in arguments and results.
 */
@Aspect
@Component
public class ActionLogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionLogAspect.class);

    private static final boolean IS_LOGGING_ENABLED = true;
    private static final String DEFAULT_LOG_LEVEL = "INFO";
    private static final String LOG_PREFIX = "[SpringKit] ";

    private final SensitiveDataMasker sensitiveDataMasker;

    public ActionLogAspect(SensitiveDataMasker sensitiveDataMasker) {
        this.sensitiveDataMasker = sensitiveDataMasker;
    }

    /**
     * Logs method execution details (start, completion, or failure) for methods annotated with @ActionLog.
     */
    @Around("execution(* *(..)) && @annotation(actionLog) && @annotation(maskSensitive)")
    public Object logAction(ProceedingJoinPoint joinPoint,
                            ActionLog actionLog,
                            MaskSensitive maskSensitive
    ) throws Throwable {
        if (!IS_LOGGING_ENABLED) {
            return joinPoint.proceed();
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        ActionLog actionLog = signature.getMethod().getAnnotation(ActionLog.class);
        if (actionLog == null) {
            return joinPoint.proceed();
        }

        String methodName = signature.toShortString();
        String logLevel = getLogLevel(actionLog);
        String description = actionLog.message();
//        MaskSensitive maskSensitive = signature.getMethod().getAnnotation(MaskSensitive.class);
        Object[] maskedArgs = sensitiveDataMasker.maskSensitiveFields(joinPoint.getArgs(), maskSensitive.maskValue());

        long startTime = System.currentTimeMillis();
        try {
            logStart(methodName, logLevel, description, maskedArgs);
            Object result = joinPoint.proceed();
            Object maskedResult = sensitiveDataMasker.maskSensitiveObject(result, maskSensitive.maskValue());
            logCompletion(methodName, logLevel, description, maskedResult, startTime);
            return result;
        } catch (Throwable throwable) {
            logFailure(methodName, description, throwable, startTime);
            throw throwable;
        }
    }

    /**
     * Retrieves the log level from the @ActionLog annotation, defaulting to "INFO" if not specified.
     */
    private String getLogLevel(ActionLog actionLog) {
        return actionLog.logLevel().isEmpty() ? DEFAULT_LOG_LEVEL : actionLog.logLevel();
    }

    /**
     * Logs the start of a method execution.
     */
    private void logStart(String methodName, String logLevel, String description, Object[] args) {
        LogUtils.log(LOGGER, logLevel, "{}ActionLog: Starting {} [desc={}], Args={}",
                LOG_PREFIX, methodName, description, args);
    }

    /**
     * Logs the successful completion of a method execution.
     */
    private void logCompletion(String methodName, String logLevel, String description, Object result, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        LogUtils.log(LOGGER, logLevel, "{}ActionLog: Completed {} [desc={}], Result={}, Duration={}ms",
                LOG_PREFIX, methodName, description, result, duration);
    }

    /**
     * Logs the failure of a method execution.
     */
    private void logFailure(String methodName, String description, Throwable throwable, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        LogUtils.log(LOGGER, "ERROR", "{}ActionLog: Failed {} [desc={}], Exception={}, Duration={}ms",
                LOG_PREFIX, methodName, description, throwable.getClass().getSimpleName(), duration);
    }
}