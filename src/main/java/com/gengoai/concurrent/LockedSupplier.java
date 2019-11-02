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
 *
 */

package com.gengoai.concurrent;

import com.gengoai.Validation;
import com.gengoai.function.SerializableSupplier;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author David B. Bracewell
 */
public class LockedSupplier<T> implements SerializableSupplier<T> {
   private final SerializableSupplier<T> supplier;
   private final ReentrantLock lock = new ReentrantLock();

   public LockedSupplier(SerializableSupplier<T> supplier) {
      this.supplier = Validation.notNull(supplier);
   }

   @Override
   public T get() {
      lock.lock();
      try {
         return supplier.get();
      } finally {
         lock.unlock();
      }
   }
}//END OF LockedSupplier
