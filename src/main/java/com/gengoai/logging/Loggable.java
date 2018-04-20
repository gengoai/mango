package com.gengoai.logging;

import java.util.logging.Level;

/**
 * <p>Defines methods for conveniently logging messages.</p>
 *
 * @author David B. Bracewell
 */
public interface Loggable {

   /**
    * Log a message with any number of arguments at the designated log level.
    *
    * @param level   the level to log at
    * @param message the message to log
    * @param args    the args associated with the message
    */
   default void log(Level level, String message, Object... args) {
      Logger.getLogger(getClass()).log(level, message, args);
   }

   /**
    * Logs a message at {@link java.util.logging.Level#FINE}.
    *
    * @param message The message accompanying the log
    * @param args    The arguments for the message.
    */
   default void logFine(String message, Object... args) {
      Logger.getLogger(getClass()).fine(message, args);
   }

   /**
    * Logs a message at {@link java.util.logging.Level#FINER}.
    *
    * @param message The message accompanying the log
    * @param args    The arguments for the message.
    */
   default void logFiner(String message, Object... args) {
      Logger.getLogger(getClass()).finer(message, args);
   }

   /**
    * Logs a message at {@link java.util.logging.Level#FINEST}.
    *
    * @param message The message accompanying the log
    * @param args    The arguments for the message.
    */
   default void logFinest(String message, Object... args) {
      Logger.getLogger(getClass()).finest(message, args);
   }

   /**
    * Logs a message at {@link java.util.logging.Level#SEVERE}.
    *
    * @param message The message accompanying the log
    * @param args    The arguments for the message.
    */
   default void logSevere(String message, Object... args) {
      Logger.getLogger(getClass()).severe(message, args);
   }

   /**
    * Logs a message at {@link java.util.logging.Level#WARNING}.
    *
    * @param message The message accompanying the log
    * @param args    The arguments for the message.
    */
   default void logWarn(String message, Object... args) {
      Logger.getLogger(getClass()).warn(message, args);
   }

   /**
    * Logs a message at {@link java.util.logging.Level#INFO}.
    *
    * @param message The message accompanying the log
    * @param args    The arguments for the message.
    */
   default void logInfo(String message, Object... args) {
      Logger.getLogger(getClass()).info(message, args);
   }

   /**
    * Logs the stack trace for a throwable at {@link java.util.logging.Level#FINE}.
    *
    * @param throwable The throwable to log
    */
   default void logFine(Throwable throwable) {
      Logger.getLogger(getClass()).fine(throwable);
   }

   /**
    * Logs the stack trace for a throwable at {@link java.util.logging.Level#FINER}.
    *
    * @param throwable The throwable to log
    */
   default void logFiner(Throwable throwable) {
      Logger.getLogger(getClass()).finer(throwable);
   }

   /**
    * Logs the stack trace for a throwable at {@link java.util.logging.Level#FINEST}.
    *
    * @param throwable The throwable to log
    */
   default void logFinest(Throwable throwable) {
      Logger.getLogger(getClass()).finest(throwable);
   }

   /**
    * Logs the stack trace for a throwable at {@link java.util.logging.Level#SEVERE}.
    *
    * @param throwable The throwable to log
    */
   default void logSevere(Throwable throwable) {
      Logger.getLogger(getClass()).severe(throwable);
   }

   /**
    * Logs the stack trace for a throwable at {@link java.util.logging.Level#WARNING}.
    *
    * @param throwable The throwable to log
    */
   default void logWarn(Throwable throwable) {
      Logger.getLogger(getClass()).warn(throwable);
   }

   /**
    * Logs a message at {@link java.util.logging.Level#INFO}.
    *
    * @param throwable The throwable to log.
    */
   default void logInfo(Throwable throwable) {
      Logger.getLogger(getClass()).info(throwable);
   }

}// END OF Loggable
