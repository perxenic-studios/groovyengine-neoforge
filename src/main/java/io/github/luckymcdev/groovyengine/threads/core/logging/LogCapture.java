/*
 * Copyright 2025 LuckyMcDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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