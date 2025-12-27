package com.order.payment.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.order.payment.constant.LoggerConstants;

import jakarta.annotation.PostConstruct;

@Component
public class LoggerUtil {

    private static final Logger logger =
            LoggerFactory.getLogger(LoggerUtil.class);

    private static LoggerUtil instance;

    @PostConstruct
    public void init() {
        instance = this;
    }

    public static LoggerUtil getInstance() {
        return instance;
    }

    public void doLog(int type, String message, Object... args) {
        String finalMessage = getLogPrefix(4) + message;
        log(type, finalMessage, args);
    }

    public void doLog(int type, String message, Throwable throwable) {
        String finalMessage = getLogPrefix(4) + message;
        logger.error(finalMessage, throwable);
    }

    private void log(int type, String message, Object... args) {
        switch (type) {
            case LoggerConstants.LTD:
                logger.debug(message, args);
                break;
            case LoggerConstants.LTW:
                logger.warn(message, args);
                break;
            case LoggerConstants.LTE:
                logger.error(message, args);
                break;
            case LoggerConstants.LTI:
                logger.info(message, args);
                break;
            default:
                logger.error(message, args);
        }
    }

    private String getLogPrefix(int depth) {
        StackTraceElement[] stackTrace =
                Thread.currentThread().getStackTrace();

        if (stackTrace.length > depth) {
            StackTraceElement element = stackTrace[depth];
            return String.format(
                "ORDER-PAYMENT LOG | Class: %s | Method: %s | Line: %d | ",
                element.getClassName(),
                element.getMethodName(),
                element.getLineNumber()
            );
        }
        return "ORDER-PAYMENT LOG | Class: Unknown | Method: Unknown | ";
    }
}
