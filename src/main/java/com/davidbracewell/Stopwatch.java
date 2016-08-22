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

package com.davidbracewell;

import lombok.NonNull;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * A simple stop watch to record time spans
 *
 * @author David B. Bracewell
 *
 */
public final class Stopwatch implements Serializable {
   private static final long serialVersionUID = 1L;
   private volatile long startTime = 0L;
   private volatile long endTime = 0L;
   private volatile boolean isRunning = false;

   public static Stopwatch createStarted() {
      return new Stopwatch().start();
   }

   public static Stopwatch createStopped() {
      return new Stopwatch();
   }


   /**
    * Starts the stop watch
    */
   public Stopwatch start() {
      isRunning = true;
      startTime = System.nanoTime();
      return this;
   }

   /**
    * Stops the stop watch
    */
   public Stopwatch stop() {
      endTime = System.nanoTime();
      isRunning = false;
      return this;
   }

   /**
    * Resets the stop watch
    */
   public Stopwatch reset() {
      isRunning = false;
      startTime = 0L;
      endTime = 0L;
      return this;
   }

   /**
    * @return The elapsed time in milliseconds
    */
   public long elapsedTime(@NonNull TimeUnit timeUnit) {
      return timeUnit.convert(timeSpan(), TimeUnit.NANOSECONDS);
   }

   public long elapsedTime() {
      return timeSpan();
   }

   private long timeSpan() {
      if (isRunning) {
         return System.nanoTime() - startTime;
      }
      return endTime - startTime;
   }


   private TimeUnit selectUnit(long time) {
      if (TimeUnit.DAYS.convert(time, TimeUnit.NANOSECONDS) > 0) {
         return TimeUnit.DAYS;
      }
      if (TimeUnit.HOURS.convert(time, TimeUnit.NANOSECONDS) > 0) {
         return TimeUnit.HOURS;
      }
      if (TimeUnit.MINUTES.convert(time, TimeUnit.NANOSECONDS) > 0) {
         return TimeUnit.MINUTES;
      }
      if (TimeUnit.SECONDS.convert(time, TimeUnit.NANOSECONDS) > 0) {
         return TimeUnit.SECONDS;
      }
      if (TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS) > 0) {
         return TimeUnit.MILLISECONDS;
      }
      if (TimeUnit.MICROSECONDS.convert(time, TimeUnit.NANOSECONDS) > 0) {
         return TimeUnit.MICROSECONDS;
      }
      return TimeUnit.NANOSECONDS;
   }


   private static String abbreviate(TimeUnit unit) {
      switch (unit) {
         case NANOSECONDS:
            return "ns";
         case MICROSECONDS:
            return "μs";
         case MILLISECONDS:
            return "ms";
         case SECONDS:
            return "s";
         case MINUTES:
            return "min";
         case HOURS:
            return "h";
         case DAYS:
            return "d";
         default:
            throw new AssertionError();
      }
   }

   @Override
   public String toString() {
      long dif = timeSpan();
      TimeUnit timeUnit = selectUnit(dif);
      double time = (double) dif / (double) TimeUnit.NANOSECONDS.convert(1L, timeUnit);
      return String.format("%.4g %s", time, abbreviate(timeUnit));
   }

}// END OF CLASS StopWatch
