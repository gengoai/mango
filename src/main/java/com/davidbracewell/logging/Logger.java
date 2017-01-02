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

package com.davidbracewell.logging;

import com.davidbracewell.string.StringUtils;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * <p> Logger wrapping the <code>java.util.logging</code> framework making certain operations much easier. In
 * particular, methods such as {@link #info(String, Object...)} provide easier ways to call {@link
 * java.util.logging.Logger#log(Level, String, Object[])}. Additionally, the level of individual loggers can be set
 * using {@link com.davidbracewell.config.Config} settings in the form of <code>fully.qualified.name.level=LEVEL</code>.</p>
 *
 * @author David B. Bracewell
 */
public final class Logger {

   /**
    * Gets the global Logger
    *
    * @return The global logger
    */
   public static Logger getGlobalLogger() {
      return LogManager.getLogManager().getGlobalLogger();
   }

   /**
    * Gets the logger for the given class.
    *
    * @param clazz The class whose logger we want
    * @return A logger associated with the given class
    */
   public static Logger getLogger(Class<?> clazz) {
      return LogManager.getLogManager().getLogger(clazz);
   }

   // The underlying logger
   private java.util.logging.Logger logger;

   /**
    * Hidden default Constructor
    */
   protected Logger() {
   }

   /**
    * Helper Constructor used to initialize the java.util.logging.Logger
    *
    * @param logger The logger to wrap
    */
   protected Logger(java.util.logging.Logger logger) {
      this.logger = logger;
   }

   /**
    * @return The name of the logger
    */
   public String getName() {
      return logger.getName();
   }

   /**
    * Determines if a given level is loggable by this logger or not
    *
    * @param level The level
    * @return True if it will log, false otherwise
    */
   public boolean isLoggable(Level level) {
      return logger.isLoggable(level);
   }

   /**
    * @return The level of the logger
    */
   public Level getLevel() {
      return logger.getLevel();
   }

   /**
    * @param level The level to set
    */
   public void setLevel(Level level) {
      logger.setLevel(level);
   }

   /**
    * Logs a message at {@link java.util.logging.Level#FINE}.
    *
    * @param message The message accompanying the log
    * @param args    The arguments for the message.
    */
   public void fine(String message, Object... args) {
      log(Level.FINE, message, args);
   }

   /**
    * Logs the stack trace for a throwable at {@link java.util.logging.Level#FINE}.
    *
    * @param t The throwable to log
    */
   public void fine(Throwable t) {
      log(Level.FINE, StringUtils.EMPTY, t);
   }

   /**
    * Logs a message at {@link java.util.logging.Level#FINER}.
    *
    * @param message The message accompanying the log
    * @param args    The arguments for the message.
    */
   public void finer(String message, Object... args) {
      log(Level.FINER, message, args);
   }

   /**
    * Logs the stack trace for a throwable at {@link java.util.logging.Level#FINER}.
    *
    * @param t The throwable to log
    */
   public void finer(Throwable t) {
      log(Level.FINER, StringUtils.EMPTY, t);
   }

   /**
    * Logs a message at {@link java.util.logging.Level#FINEST}.
    *
    * @param message The message accompanying the log
    * @param args    The arguments for the message.
    */
   public void finest(String message, Object... args) {
      log(Level.FINEST, message, args);
   }

   /**
    * Logs the stack trace for a throwable at {@link java.util.logging.Level#FINEST}.
    *
    * @param t The throwable to log
    */
   public void finest(Throwable t) {
      log(Level.FINEST, StringUtils.EMPTY, t);
   }

   /**
    * Logs a message at {@link java.util.logging.Level#INFO}.
    *
    * @param message The message accompanying the log
    * @param args    The arguments for the message.
    */
   public void info(String message, Object... args) {
      log(Level.INFO, message, args);
   }

   /**
    * Logs a message at {@link java.util.logging.Level#INFO}.
    *
    * @param t The throwable to log.
    */
   public void info(Throwable t) {
      log(Level.INFO, StringUtils.EMPTY, t);
   }

   /**
    * Logs a message at a given level.
    *
    * @param level   The level to log the message at.
    * @param message The message accompanying the log
    * @param args    The arguments for the message.
    */
   public void log(Level level, String message, Object... args) {
      if (logger.isLoggable(level)) {
         String errorMessage = "";
         Throwable thrown = null;
         Object[] params = null;

         if (args == null || args.length == 0) {
            errorMessage = message;
         } else if (args[0] instanceof Throwable) {
            thrown = (Throwable) args[0];
            if (args.length > 1) {
               params = new Object[args.length - 1];
               System.arraycopy(args, 1, params, 0, args.length - 1);
            }
         } else if (args[args.length - 1] instanceof Throwable) {
            params = new Object[args.length - 1];
            System.arraycopy(args, 0, params, 0, args.length - 1);
            thrown = (Throwable) args[args.length - 1];
         } else {
            params = new Object[args.length];
            System.arraycopy(args, 0, params, 0, args.length);
         }

         if (params != null) {
            errorMessage = MessageFormat.format(message, params);
         }

         LogRecord record = new LogRecord(level, errorMessage);
         record.setLoggerName(logger.getName());
         record.setThrown(thrown);
         record.setParameters(args);
         logger.log(record);
      }
   }

   /**
    * Logs a message at {@link java.util.logging.Level#SEVERE}.
    *
    * @param message The message accompanying the log
    * @param args    The arguments for the message.
    */
   public void severe(String message, Object... args) {
      log(Level.SEVERE, message, args);
   }

   /**
    * Logs the stack trace for a throwable at {@link java.util.logging.Level#SEVERE}.
    *
    * @param t The throwable to log
    */
   public void severe(Throwable t) {
      log(Level.SEVERE, StringUtils.EMPTY, t);
   }

   /**
    * Logs a message at {@link java.util.logging.Level#WARNING}.
    *
    * @param message The message accompanying the log
    * @param args    The arguments for the message.
    */
   public void warn(String message, Object... args) {
      log(Level.WARNING, message, args);
   }

   /**
    * Logs the stack trace for a throwable at {@link java.util.logging.Level#WARNING}.
    *
    * @param t The throwable to log
    */
   public void warn(Throwable t) {
      log(Level.WARNING, StringUtils.EMPTY, t);
   }

}// END OF CLASS Logger
