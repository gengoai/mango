package com.gengoai;

import com.gengoai.concurrent.Threads;

import java.io.Serializable;
import java.time.Duration;
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

   public static void main(String[] args) throws Exception {
      Stopwatch sw = Stopwatch.createStarted();
      Threads.sleep(10000);
      System.out.println(sw);
   }

   @Override
   public String toString() {
      return Duration.ofNanos(getElapsedTime()).toString()
                     .substring(2)
                     .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                     .toLowerCase();
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
