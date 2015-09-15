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

import java.util.Collections;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * Logging Manager for Espresso loggers.
 *
 * @author David B. Bracewell
 */
public class LogManager {

  private static volatile LogManager INSTANCE = null;

  /**
   * Hidden Constructor
   */
  private LogManager() {

  }

  /**
   * Gets log manager.
   *
   * @return The log manager
   */
  public static LogManager getLogManager() {
    if (INSTANCE == null) {
      synchronized (LogManager.class) {
        if (INSTANCE == null) {
          INSTANCE = new LogManager();
          java.util.logging.Logger root = java.util.logging.LogManager
              .getLogManager()
              .getLogger("");
          for (Handler h : root.getHandlers()) {
            h.setFormatter(new LogFormatter());
            h.setLevel(Level.INFO);
          }
        }
      }
    }
    return INSTANCE;
  }


  /**
   * Sets formatter.
   *
   * @param formatter the formatter
   */
  public synchronized static void setFormatter(Formatter formatter) {
    LogManager.getLogManager();
    java.util.logging.Logger root = java.util.logging.LogManager.getLogManager().getLogger(StringUtils.EMPTY);
    for (Handler h : root.getHandlers()) {
      h.setFormatter(formatter);
    }
  }

  /**
   * Clear handlers.
   */
  public synchronized static void clearHandlers() {
    LogManager.getLogManager();
    java.util.logging.Logger root = java.util.logging.LogManager.getLogManager().getLogger(StringUtils.EMPTY);
    Handler[] handlers = root.getHandlers();
    for (Handler h : handlers) {
      root.removeHandler(h);
    }
  }

  /**
   * Add handler.
   *
   * @param handler the handler
   */
  public synchronized static void addHandler(Handler handler) {
    LogManager.getLogManager();
    java.util.logging.Logger root = java.util.logging.LogManager.getLogManager().getLogger(StringUtils.EMPTY);
    root.addHandler(handler);
  }

  /**
   * Remove handlers of type.
   *
   * @param handlerClass the handler class
   */
  public synchronized void removeHandlersOfType(Class<?> handlerClass) {
    java.util.logging.Logger root = java.util.logging.LogManager.getLogManager().getLogger(StringUtils.EMPTY);
    for (Handler handler : root.getHandlers()) {
      if (handlerClass.isAssignableFrom(handler.getClass())) {
        root.removeHandler(handler);
      }
    }
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

  private String getParentLogger(String logger) {
    int index = logger.lastIndexOf('.');
    if (index == -1) {
      return StringUtils.EMPTY;
    }
    return logger.substring(0, index);
  }

  private Logger getLogger(String name) {
    java.util.logging.Logger jul = java.util.logging.LogManager.getLogManager().getLogger(name);
    if (jul == null) {
      jul = java.util.logging.Logger.getLogger(name);
//      if (Config.hasProperty(name + ".level")) {
//        jul.setLevel(Level.parse(Config.get(name + ".level").asString()));
//      } else {
//        String parent = getParentLogger(name);
//        while (true) {
//          if (Strings.isNullOrEmpty(parent)) {
//            break;
//          } else if (java.util.logging.LogManager.getLogManager().getLogger(parent) != null) {
//            jul.setLevel(java.util.logging.LogManager.getLogManager().getLogger(parent).getLevel());
//            break;
//          } else if (Config.hasProperty(parent + ".level")) {
//            jul.setLevel(Level.parse(Config.get(parent + ".level").asString()));
//            break;
//          }
//          parent = getParentLogger(parent);
//        }
//      }
      java.util.logging.LogManager.getLogManager().addLogger(jul);
    }
    return new Logger(jul);
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
   * @param level The level to set the logger at
   */
  public void setLevel(String logger, Level level) {
    Logger log = getLogger(logger);
    log.setLevel(level);
    for( String loggerName : getLoggerNames() ){
      if( loggerName.startsWith(logger) && !loggerName.equals(logger) ){
        getLogger(loggerName).setLevel(level);
      }
    }
  }

}// END OF CLASS LogManager
