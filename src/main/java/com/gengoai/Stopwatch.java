package com.gengoai;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import static com.gengoai.Validation.checkState;
import static com.gengoai.Validation.notNull;

/**
 * <p>Tracks start and ending times to determine total time taken.</p>
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
    * Start the stopwatch.
    */
   public void start() {
      checkState(!isRunning, "Cannot start an already started Stopwatch");
      this.isRunning = true;
      this.start = getSystemNano();
   }

   /**
    * Stop the stopwatch.
    */
   public void stop() {
      checkState(isRunning, "Cannot stop an already stopped Stopwatch");
      this.isRunning = false;
      elapsedTime += (getSystemNano() - this.start);
   }

   /**
    * Reset the stopwatch.
    */
   public void reset() {
      checkState(isRunning, "Cannot stop an already stopped Stopwatch");
      this.isRunning = false;
      this.start = -1;
      this.elapsedTime = 0L;
   }

   /**
    * Gets the elapsed time in given time units
    *
    * @param timeUnit the time unit
    * @return the elapsed time in the given time unit
    */
   public long elapsed(TimeUnit timeUnit) {
      return notNull(timeUnit).convert(getElapsedTime(), TimeUnit.NANOSECONDS);
   }

   /**
    * Gets elapsed time in nano seconds
    *
    * @return the elapsed time in nano seconds
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
    * Create a stopwatch that is started.
    *
    * @return the stopwatch
    */
   public static Stopwatch createStarted() {
      return new Stopwatch(true);
   }

   /**
    * Create a stopwatch that is stopped.
    *
    * @return the stopwatch
    */
   public static Stopwatch createStopped() {
      return new Stopwatch(false);
   }


}//END OF Stopwatch
