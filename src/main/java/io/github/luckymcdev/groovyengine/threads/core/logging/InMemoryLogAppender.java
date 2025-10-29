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

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Plugin(name = "InMemoryLogAppender", category = "Core", elementType = "appender", printObject = true)
public class InMemoryLogAppender extends AbstractAppender {

    private static final List<String> logLines = new LinkedList<>();
    private static final int MAX_LINES = 1000;

    protected InMemoryLogAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    /**
     * Returns a copy of the current log lines.
     * The log lines are returned as a new {@link List} containing the same elements
     * as the current log lines in the order they were added.
     * The returned list is safe to modify without affecting the internal state of the appender.
     * @return a copy of the current log lines
     */
    public static List<String> getLogLines() {
        synchronized (logLines) {
            return new LinkedList<>(logLines);
        }
    }

    /**
     * Creates an instance of the InMemoryLogAppender plugin.
     *
     * @param name the name of the appender
     * @return an instance of the InMemoryLogAppender plugin
     */
    @PluginFactory
    public static Appender createAppender(
            @PluginAttribute("name") String name
    ) {
        return new InMemoryLogAppender(name, null, null, true);
    }

    /**
     * Appends a log event to the in-memory log.
     * The event is converted to a string with the format
     * <code>LEVEL | LOGGER_NAME | MESSAGE</code> and added to the end of the log.
     * If the log exceeds the maximum number of lines, the oldest line is removed.
     * This method is thread-safe.
     * @param event the log event to append
     */
    @Override
    public void append(LogEvent event) {
        synchronized (logLines) {
            String message = event.getLevel() + " | " + event.getLoggerName() + " | " + event.getMessage().getFormattedMessage();
            logLines.add(message);
            if (logLines.size() > MAX_LINES) {
                logLines.remove(0);
            }
        }
    }
}