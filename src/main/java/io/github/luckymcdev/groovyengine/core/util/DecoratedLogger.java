package io.github.luckymcdev.groovyengine.core.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DecoratedLogger {

    private final Logger logger;

    public DecoratedLogger(Class<?> clazz) {
        this.logger = LogManager.getLogger(clazz);
    }

    private String prefix(String msg) {
        return "[GroovyEngine] " + msg;
    }

    public void info(String msg, Object... args) {
        logger.info(prefix(msg), args);
    }

    public void warn(String msg, Object... args) {
        logger.warn(prefix(msg), args);
    }

    public void error(String msg, Object... args) {
        logger.error(prefix(msg), args);
    }

    public void debug(String msg, Object... args) {
        logger.debug(prefix(msg), args);
    }
}
