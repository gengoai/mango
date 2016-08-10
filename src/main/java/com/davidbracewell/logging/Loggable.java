package com.davidbracewell.logging;

import java.util.logging.Level;

/**
 * The interface Loggable.
 *
 * @author David B. Bracewell
 */
public interface Loggable {

  /**
   * Log.
   *
   * @param level   the level
   * @param message the message
   * @param args    the args
   */
  default void log(Level level, String message, Object... args) {
    Logger.getLogger(getClass()).log(level, message, args);
  }

  /**
   * Log fine.
   *
   * @param message the message
   * @param args    the args
   */
  default void logFine(String message, Object... args) {
    Logger.getLogger(getClass()).fine(message, args);
  }

  /**
   * Log finer.
   *
   * @param message the message
   * @param args    the args
   */
  default void logFiner(String message, Object... args) {
    Logger.getLogger(getClass()).finer(message, args);
  }

  /**
   * Log finest.
   *
   * @param message the message
   * @param args    the args
   */
  default void logFinest(String message, Object... args) {
    Logger.getLogger(getClass()).finest(message, args);
  }

  /**
   * Log severe.
   *
   * @param message the message
   * @param args    the args
   */
  default void logSevere(String message, Object... args) {
    Logger.getLogger(getClass()).severe(message, args);
  }

  /**
   * Log warning.
   *
   * @param message the message
   * @param args    the args
   */
  default void logWarning(String message, Object... args) {
    Logger.getLogger(getClass()).warn(message, args);
  }

  /**
   * Log info.
   *
   * @param message the message
   * @param args    the args
   */
  default void logInfo(String message, Object... args) {
    Logger.getLogger(getClass()).info(message, args);
  }

  /**
   * Log fine.
   *
   * @param throwable the throwable
   */
  default void logFine(Throwable throwable) {
    Logger.getLogger(getClass()).fine(throwable);
  }

  /**
   * Log finer.
   *
   * @param throwable the throwable
   */
  default void logFiner(Throwable throwable) {
    Logger.getLogger(getClass()).finer(throwable);
  }

  /**
   * Log finest.
   *
   * @param throwable the throwable
   */
  default void logFinest(Throwable throwable) {
    Logger.getLogger(getClass()).finest(throwable);
  }

  /**
   * Log severe.
   *
   * @param throwable the throwable
   */
  default void logSevere(Throwable throwable) {
    Logger.getLogger(getClass()).severe(throwable);
  }

  /**
   * Log warning.
   *
   * @param throwable the throwable
   */
  default void logWarning(Throwable throwable) {
    Logger.getLogger(getClass()).warn(throwable);
  }

  /**
   * Log info.
   *
   * @param throwable the throwable
   */
  default void logInfo(Throwable throwable) {
    Logger.getLogger(getClass()).info(throwable);
  }

}// END OF Loggable
