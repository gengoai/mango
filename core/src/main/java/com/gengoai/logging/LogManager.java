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

package com.gengoai.logging;

import com.gengoai.SystemInfo;
import com.gengoai.config.Config;
import com.gengoai.io.Resources;
import com.gengoai.string.Strings;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * Wrapper around the Java LogManager that does default setup if no logging config file is defined. Note: that if a
 * config file for java.util.logging is specified default initialization will not place. File handlers can be added
 * using a common based name and the log directory will be read from the config property
 * <code>com.gengoai.logging.dir</code> and default to <code>%HOME/logs/</code>. The file handler will produce up
 * to 100MB logs and rotate over 50 log files.
 *
 * @author David B. Bracewell
 */
public final class LogManager {

   private static final LogManager INSTANCE = new LogManager();

   private LogManager() {
      if (System.getProperty("java.util.logging.config.file") == null) {
         java.util.logging.Logger root = java.util.logging.LogManager
                                            .getLogManager()
                                            .getLogger("");
         for (Handler h : root.getHandlers()) {
            h.setFormatter(new LogFormatter());
            h.setLevel(Level.ALL);
         }
      }
   }

   /**
    * Adds a file handler that writes to the location specified in <code>com.gengoai.logging.dir</code> or if not
    * set <code>USER_HOME/logs/</code>. The filenames are in the form of <code>basename%g</code> where %g is the rotated
    * file number. Max file size is 100MB and 50 files will be used.
    */
   public synchronized static void addFileHandler(String basename) throws IOException {
      String dir = Config.get("com.gengoai.logging.dir").asString(SystemInfo.USER_HOME + "/logs/");
      Resources.from(dir).mkdirs();
      FileHandler fh = new FileHandler(dir + "/" + basename + "%g.log", 100000000, 50, false);
      fh.setFormatter(new LogFormatter());
      addHandler(fh);
   }

   /**
    * Adds a handler to the root.
    *
    * @param handler the handler to add
    */
   public synchronized static void addHandler(Handler handler) {
      java.util.logging.Logger root = java.util.logging.LogManager.getLogManager().getLogger(Strings.EMPTY);
      root.addHandler(handler);
   }

   /**
    * Clears all handlers from the logger
    */
   public synchronized static void clearHandlers() {
      java.util.logging.Logger root = java.util.logging.LogManager.getLogManager().getLogger(Strings.EMPTY);
      Handler[] handlers = root.getHandlers();
      for (Handler h : handlers) {
         root.removeHandler(h);
      }
   }

   /**
    * Gets the log manager instance
    *
    * @return The log manager instance
    */
   public static LogManager getLogManager() {
      return INSTANCE;
   }

   /**
    * Sets the formatter to use for all handlers.
    *
    * @param formatter the formatter to use
    */
   public synchronized static void setFormatter(Formatter formatter) {
      java.util.logging.Logger root = java.util.logging.LogManager.getLogManager().getLogger(Strings.EMPTY);
      for (Handler h : root.getHandlers()) {
         h.setFormatter(formatter);
      }
   }

   /**
    * Gets the global Logger
    *
    * @return The global logger
    */
   public Logger getGlobalLogger() {
      return new Logger(java.util.logging.Logger
                           .getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME));
   }

   private Logger getLogger(String name) {
      java.util.logging.Logger jul = java.util.logging.LogManager.getLogManager().getLogger(name);
      if (jul == null) {
         jul = java.util.logging.Logger.getLogger(name);
         java.util.logging.LogManager.getLogManager().addLogger(jul);
      }
      return new Logger(jul);
   }

   /**
    * Gets the logger for the given class.
    *
    * @param clazz The class whose logger we want
    * @return A logger associated with the given class
    */
   public Logger getLogger(Class<?> clazz) {
      if (clazz == null) {
         return getGlobalLogger();
      }
      return getLogger(clazz.getName());
   }

   /**
    * Gets an Enumeration of all of the current loggers.
    *
    * @return An of logger names
    */
   public List<String> getLoggerNames() {
      return Collections.list(java.util.logging.LogManager.getLogManager().getLoggerNames());
   }

   /**
    * Sets the level of a logger
    *
    * @param logger The name of the logger to set the level for.
    * @param level  The level to set the logger at
    */
   public void setLevel(String logger, Level level) {
      Logger log = getLogger(logger);
      log.setLevel(level);
      for (String loggerName : getLoggerNames()) {
         if (loggerName.startsWith(logger) && !loggerName.equals(logger)) {
            getLogger(loggerName).setLevel(level);
         }
      }
   }

}// END OF CLASS LogManager
