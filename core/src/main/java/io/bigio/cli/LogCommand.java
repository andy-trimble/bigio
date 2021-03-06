/*
 * Copyright (c) 2015, Archarithms Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */

package io.bigio.cli;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import io.bigio.CommandLine;
import io.bigio.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the "log" CLI command. This command will set the console log level.
 * 
 * @author Andy Trimble
 */
@Component
public class LogCommand implements CommandLine {

    private static final Logger LOG = LoggerFactory.getLogger(LogCommand.class);

    private static final String USAGE = "Usage: log [all|trace|debug|info|warn|error|off|none]";

    /**
     * Get the command string.
     * 
     * @return the command.
     */
    @Override
    public String getCommand() {
        return "log";
    }

    /**
     * Execute the command.
     * 
     * @param args the arguments to the command (if any).
     */
    @Override
    public void execute(String... args) {
        if (args.length < 2) {
            System.out.println(USAGE);
        } else {
            setLoggingLevel(args[1]);
        }
    }

    /**
     * Return the help/description string for display.
     * 
     * @return the help/description string
     */
    @Override
    public String help() {
        return "Sets the console log level. " + USAGE;
    }

    private void setLoggingLevel(String level) {

        ch.qos.logback.classic.Logger rootLogger = 
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        ThresholdFilter threshold = null;
            
        for(Filter<ILoggingEvent> filter : rootLogger.getAppender("CONSOLE").getCopyOfAttachedFiltersList()) {
            if(filter instanceof ThresholdFilter) {
                threshold = (ThresholdFilter)filter;
                break;
            }
        }

        if(threshold == null) {
            return;
        }

        LOG.info("Setting log level to '" + level + "'");

        if("all".equalsIgnoreCase(level)) {
            threshold.setLevel(Level.ALL.levelStr);
        } else if("trace".equalsIgnoreCase(level)) {
            threshold.setLevel(Level.TRACE.levelStr);
        } else if("debug".equalsIgnoreCase(level)) {
            threshold.setLevel(Level.DEBUG.levelStr);
        } else if("info".equalsIgnoreCase(level)) {
            threshold.setLevel(Level.INFO.levelStr);
        } else if("warn".equalsIgnoreCase(level)) {
            threshold.setLevel(Level.WARN.levelStr);
        } else if("error".equalsIgnoreCase(level)) {
            threshold.setLevel(Level.ERROR.levelStr);
        } else if("off".equalsIgnoreCase(level)) {
            threshold.setLevel(Level.OFF.levelStr);
        } else if("none".equalsIgnoreCase(level)) {
            threshold.setLevel(Level.OFF.levelStr);
        }
    }
}
