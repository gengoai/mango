/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 *
 */
package com.gengoai.logging;


import com.gengoai.SystemInfo;
import com.gengoai.io.QuietIO;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * <p>Custom logging formatter in the form <code>[time] [level] [class:line] message</code>. The class name and line
 * number only show on warning and severe level.</p>
 *
 * @author David B. Bracewell
 */
public class LogFormatter extends Formatter {

   private final DateFormat dateFormatter = new SimpleDateFormat("H:mm:ss");

   @Override
   public String format(LogRecord arg0) {
      StringBuilder msg = new StringBuilder();
      String className;
      int lineNumber = -1;

      Throwable thrown = arg0.getThrown();
      if (thrown == null) {
         className = arg0.getLoggerName();
      } else {
         StackTraceElement element = thrown.getStackTrace()[0];
         className = element.getClassName();
         lineNumber = element.getLineNumber();
      }

      Date date = new Date();
      date.setTime(arg0.getMillis());
      msg.append('[')
         .append(dateFormatter.format(date))
         .append("] [")
         .append(arg0.getLevel())
         .append("] ");

      if (arg0.getLevel() == Level.WARNING || arg0.getLevel() == Level.SEVERE) {
         msg.append("[").append(className);
         if (lineNumber >= 0) {
            msg.append(':')
               .append(lineNumber);
         }
         msg.append("] ");
      }
      msg.append(MessageFormat.format(arg0.getMessage(), arg0.getParameters()));

      if (thrown != null) {
         StringWriter sw = new StringWriter();
         thrown.printStackTrace(new PrintWriter(sw));
         msg.append(sw.toString());
         QuietIO.closeQuietly(sw);
      } else {
         msg.append(SystemInfo.LINE_SEPARATOR);
      }

      return msg.toString();
   }

}//END OF LogFormatter
