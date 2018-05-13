package com.gengoai;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * The type Stopwatch.
 *
 * @author David B. Bracewell
 */
public class Stopwatch implements Serializable {
   private static final long serialVersionUID = 1L;
   private long start = -1L;
   private long elapsedTime = 0L;
   private boolean isRunning = false;


   private Stopwatch(boolean started) {
      if (started) {
         start();
      }
   }

   private long getSystemNano() {
      return TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis());
   }

   /**
    * Start.
    */
   public void start() {
      Validation.checkState(!isRunning, "Cannot start an already started Stopwatch");
      this.isRunning = true;
      this.start = getSystemNano();
   }

   /**
    * Stop.
    */
   public void stop() {
      Validation.checkState(isRunning, "Cannot stop an already stopped Stopwatch");
      this.isRunning = false;
      elapsedTime += (getSystemNano() - this.start);
   }

   /**
    * Reset.
    */
   public void reset() {
      Validation.checkState(isRunning, "Cannot stop an already stopped Stopwatch");
      this.isRunning = false;
      this.start = -1;
      this.elapsedTime = 0L;
   }

   /**
    * Gets elapsed time.
    *
    * @return the elapsed time
    */
   public long getElapsedTime() {
      return isRunning
             ? elapsedTime + (TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()) - this.start)
             : elapsedTime;
   }

   @Override
   public String toString() {
      long elapsed = getElapsedTime();

      TimeUnit timeUnit = chooseLargestUnit(elapsed);
      double value = (double) elapsed / TimeUnit.NANOSECONDS.convert(1, timeUnit);

      return String.format("%.4g %s", value, getAbbreviation(timeUnit));
   }

   private static String getAbbreviation(TimeUnit timeUnit) {
      switch (timeUnit) {
         case DAYS:
            return "d";
         case HOURS:
            return "h";
         case MINUTES:
            return "m";
         case SECONDS:
            return "s";
         case MILLISECONDS:
            return "ms";
         case MICROSECONDS:
            return "\u03bcs";
         default:
            return "ns";
      }
   }

   private static TimeUnit chooseLargestUnit(long nano) {
      if (TimeUnit.DAYS.convert(nano, TimeUnit.NANOSECONDS) > 0) {
         return TimeUnit.DAYS;
      }
      if (TimeUnit.HOURS.convert(nano, TimeUnit.NANOSECONDS) > 0) {
         return TimeUnit.HOURS;
      }
      if (TimeUnit.MINUTES.convert(nano, TimeUnit.NANOSECONDS) > 0) {
         return TimeUnit.MINUTES;
      }
      if (TimeUnit.SECONDS.convert(nano, TimeUnit.NANOSECONDS) > 0) {
         return TimeUnit.SECONDS;
      }
      if (TimeUnit.MILLISECONDS.convert(nano, TimeUnit.NANOSECONDS) > 0) {
         return TimeUnit.MICROSECONDS;
      }
      return TimeUnit.NANOSECONDS;
   }


   /**
    * Create started stopwatch.
    *
    * @return the stopwatch
    */
   public static Stopwatch createStarted() {
      return new Stopwatch(true);
   }

   /**
    * Create stopped stopwatch.
    *
    * @return the stopwatch
    */
   public static Stopwatch createStopped() {
      return new Stopwatch(false);
   }


}//END OF Stopwatch
