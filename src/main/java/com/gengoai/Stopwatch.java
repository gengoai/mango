package com.gengoai;

import com.gengoai.string.Strings;

import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

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
   /**
    * The Name of the stopwatch
    */
   public final String name;


   private Stopwatch(boolean started, String name) {
      this.name = name;
      if (started) {
         start();
      }
   }


   /**
    * Calculates the time to execute the given runnable
    *
    * @param runnable the runnable to time
    * @return the stopwatch in a stopped state
    */
   public static Stopwatch timeIt(Runnable runnable) {
      Stopwatch toReturn = createStarted();
      runnable.run();
      toReturn.stop();
      return toReturn;
   }

   /**
    * Calculates the time to execute the given runnable nTrial times
    *
    * @param nTrials  the nunber of times to execute the runnable
    * @param runnable the runnable to time
    * @return the stopwatch in a stopped state
    */
   public static Stopwatch timeIt(int nTrials, Runnable runnable) {
      Stopwatch toReturn = createStarted();
      IntStream.range(0, nTrials).forEach(i -> runnable.run());
      toReturn.stop();
      return toReturn.averageTime(nTrials);
   }


   /**
    * Sets the elapsed time of the stopwatch to <code>elapsed time / trials</code>
    *
    * @param trials the number of trials to use to average the time
    * @return this stopwatch
    */
   public Stopwatch averageTime(long trials) {
      checkState(!isRunning, "Can only average when stopped");
      elapsedTime = (long) Math.floor((double) elapsedTime / trials);
      return this;
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


   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      if (Strings.isNotNullOrBlank(name)) {
         stringBuilder.append(name).append(": ");
      }
      stringBuilder.append(Duration.ofNanos(getElapsedTime()).toString()
                                   .substring(2)
                                   .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                                   .toLowerCase());
      return stringBuilder.toString();
   }


   /**
    * Create a stopwatch that is started.
    *
    * @param name the name of the stopwatch for reporting purposes
    * @return the stopwatch
    */
   public static Stopwatch createStarted(String name) {
      return new Stopwatch(true, name);
   }


   /**
    * Create a stopwatch that is started.
    *
    * @return the stopwatch
    */
   public static Stopwatch createStarted() {
      return new Stopwatch(true, null);
   }

   /**
    * Create a stopwatch that is stopped.
    *
    * @param name the name of the stopwatch for reporting purposes
    * @return the stopwatch
    */
   public static Stopwatch createStopped(String name) {
      return new Stopwatch(false, name);
   }

   /**
    * Create a stopwatch that is stopped.
    *
    * @return the stopwatch
    */
   public static Stopwatch createStopped() {
      return new Stopwatch(false, null);
   }
}//END OF Stopwatch
