/*
 * Copyright 2017 jagrosh.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.mhghapp.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;
import static ch.qos.logback.core.pattern.color.ANSIConstants.*;
import static ch.qos.logback.classic.Level.*;

/**
 * A simple highlighter for console text using logback classic.
 *
 * @author Kaidan Gustave
 */
public class SimpleHL extends ForegroundCompositeConverterBase<ILoggingEvent>
{
    @Override
    protected String getForegroundColorCode(ILoggingEvent event)
    {
        switch(event.getLevel().levelInt)
        {
            case ERROR_INT:
                return BOLD + RED_FG;
            case WARN_INT:
                return RED_FG;
            case INFO_INT:
                return GREEN_FG;
            case DEBUG_INT:
                return YELLOW_FG;
            default:
                return DEFAULT_FG;
        }
    }
}
