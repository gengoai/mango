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

package com.gengoai.mango.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * Convenience methods for sleeping threads.
 *
 * @author David B. Bracewell
 */
public interface Threads {

   /**
    * <p> Sleeps the thread suppressing any errors. </p>
    *
    * @param milliseconds The amount of time in milliseconds to sleep
    */
   static void sleep(long milliseconds) {
      if (milliseconds <= 0) {
         return;
      }
      try {
         Thread.sleep(milliseconds);
      } catch (InterruptedException e) {
         //no op
      }
   }

   /**
    * <p> Sleeps the thread suppressing any errors for a given time unit. </p>
    *
    * @param time     The amount of time to sleep
    * @param timeUnit The TimeUnit that the time is in
    */
   static void sleep(long time, TimeUnit timeUnit) {
      sleep(timeUnit.toMillis(time));
   }


}// END OF INTERFACE Threads
