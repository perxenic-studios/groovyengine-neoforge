package io.github.luckymcdev.groovyengine.threads.core.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class LogCapture {

    /**
     * Hooks into the root Logger of Log4j to capture logs into an in-memory appender.
     * This method should be called before any logging is done.
     */
    public static void hookLog4j() {
        Logger rootLogger = (Logger) LogManager.getRootLogger();

        InMemoryLogAppender appender = (InMemoryLogAppender) InMemoryLogAppender.createAppender("InMemoryAppender");
        appender.start();

        rootLogger.addAppender(appender);
    }
}