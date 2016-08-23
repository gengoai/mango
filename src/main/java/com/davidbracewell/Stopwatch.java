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
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

import static com.davidbracewell.Validations.validateState;

/**
 * A simple stop watch to record time spans
 *
 * @author David B. Bracewell
 */
public final class Stopwatch implements Serializable {
  private static final long serialVersionUID = 1L;
  private volatile Instant startTime = null;
  private volatile Instant endTime = null;
  private volatile boolean isRunning = false;


  /**
   * Create started stopwatch.
   *
   * @return the stopwatch
   */
  public static Stopwatch createStarted() {
    return new Stopwatch().start();
  }

  /**
   * Create stopped stopwatch.
   *
   * @return the stopwatch
   */
  public static Stopwatch createStopped() {
    return new Stopwatch();
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

  /**
   * Starts the stop watch
   *
   * @return the stopwatch
   */
  public Stopwatch start() {
    validateState(!isRunning, "Stopwatch is already running.");
    isRunning = true;
    startTime = Instant.now();
    return this;
  }

  /**
   * Stops the stop watch
   *
   * @return the stopwatch
   */
  public Stopwatch stop() {
    validateState(isRunning, "Stopwatch is not running.");
    endTime = Instant.now();
    isRunning = false;
    return this;
  }

  /**
   * Resets the stop watch
   *
   * @return the stopwatch
   */
  public Stopwatch reset() {
    isRunning = false;
    startTime = null;
    endTime = null;
    return this;
  }


  public Duration duration() {
    validateState(startTime != null, "Stopwatch has not been started.");
    return Duration.between(startTime, endTime != null ? endTime : Instant.now());
  }

  /**
   * Elapsed time long.
   *
   * @param timeUnit the time unit
   * @return The elapsed time in milliseconds
   */
  public long elapsed(@NonNull TemporalUnit timeUnit) {
    return duration().get(timeUnit);
  }

  public long elapsed(@NonNull TimeUnit timeUnit) {
    return timeUnit.convert(duration().toNanos(), TimeUnit.NANOSECONDS);
  }

  /**
   * Elapsed time long.
   *
   * @return the long
   */
  public long elapsed() {
    return duration().toNanos();
  }

  private TimeUnit selectUnit(Duration duration) {
    if (duration.toDays() > 0) {
      return TimeUnit.DAYS;
    } else if (duration.toMinutes() > 0) {
      return TimeUnit.MINUTES;
    } else if (duration.getSeconds() > 0) {
      return TimeUnit.SECONDS;
    } else if (duration.toMillis() > 0) {
      return TimeUnit.MILLISECONDS;
    } else if (duration.get(ChronoUnit.MICROS) > 0) {
      return TimeUnit.MICROSECONDS;
    }
    return TimeUnit.NANOSECONDS;
  }

  @Override
  public String toString() {
    Duration duration = duration();
    TimeUnit timeUnit = selectUnit(duration);
    double time = (double) duration.toNanos() / (double) TimeUnit.NANOSECONDS.convert(1L, timeUnit);
    return String.format("%.4g %s", time, abbreviate(timeUnit));
  }

}// END OF CLASS StopWatch
