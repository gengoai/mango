/*
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

package com.gengoai;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MangoLogFormatter extends Formatter {
   private static final String FORMAT = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS [%2$s %3$s] %4$s %5$s%n";

   @Override
   public String format(LogRecord record) {
      String throwable = "";
      if(record.getThrown() != null) {
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         pw.println();
         record.getThrown().printStackTrace(pw);
         pw.close();
         throwable = sw.toString();
      }
      return String.format(FORMAT,
                           ZonedDateTime.ofInstant(record.getInstant(),
                                                   ZoneId.systemDefault()),
                           record.getLoggerName(),
                           record.getLevel(),
                           formatMessage(record),
                           throwable);
   }

}//END OF MangoLogFormatter
