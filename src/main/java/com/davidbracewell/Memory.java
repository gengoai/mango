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

/**
 * <p>Utility for getting statistics about memory.</p>
 *
 * @author David B. Bracewell
 */
public final class Memory {

  private static final long KB = 1024;
  private static final double LOG_KB = Math.log(KB);
  private static final String[] measures = {"K", "M", "G", "T", "P", "E"};

  private Memory() {
    throw new IllegalAccessError();
  }

  /**
   * Wrapper around <code>Runtime.getRuntime().maxMemory()</code>
   *
   * @return the maximum amount of memory that the Java virtual machine will attempt to use
   */
  public static long max() {
    return Runtime.getRuntime().maxMemory();
  }

  /**
   * Wrapper around <code>Runtime.getRuntime().freeMemory()</code>
   *
   * @return the amount of free memory in the Java Virtual Machine.
   */
  public static long free() {
    return Runtime.getRuntime().freeMemory();
  }

  /**
   * Wrapper around <code>Runtime.getRuntime().totalMemory()</code>
   *
   * @return the total amount of memory in the Java virtual machine.
   */
  public static long total() {
    return Runtime.getRuntime().totalMemory();
  }

  /**
   * The amount of memory available to the jvm that is in use
   *
   * @return The amount of memory available to the jvm that is in use
   */
  public static long used() {
    return total() - free();
  }

  /**
   * Wrapper around <code>Runtime.getRuntime().maxMemory()</code>
   *
   * @return the maximum amount of memory that the Java virtual machine will attempt to use
   */
  public static String maxReadable() {
    return humanReadable(Runtime.getRuntime().maxMemory());
  }

  /**
   * Wrapper around <code>Runtime.getRuntime().freeMemory()</code>
   *
   * @return the amount of free memory in the Java Virtual Machine.
   */
  public static String freeReadable() {
    return humanReadable(Runtime.getRuntime().freeMemory());
  }

  /**
   * Wrapper around <code>Runtime.getRuntime().totalMemory()</code>
   *
   * @return the total amount of memory in the Java virtual machine.
   */
  public static String totalReadable() {
    return humanReadable(Runtime.getRuntime().totalMemory());
  }

  /**
   * The amount of memory available to the jvm that is in use
   *
   * @return The amount of memory available to the jvm that is in use
   */
  public static String usedReadable() {
    return humanReadable(total() - free());
  }

  /**
   * Creates a human readable version of a memory amount
   *
   * @param memory The amount of memory
   * @return The human readable version of the memory
   */
  public static String humanReadable(long memory) {
    if (memory < KB) {
      return memory + " B";
    }
    int exp = (int) (Math.log(memory) / LOG_KB);
    return String.format("%.2f %siB", memory / Math.pow(KB, exp), measures[exp - 1]);
  }


}//END OF MemoryMonitor
