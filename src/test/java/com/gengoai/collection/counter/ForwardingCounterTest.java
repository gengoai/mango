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

package com.gengoai.collection.counter;

import com.gengoai.io.resource.Resource;
import com.gengoai.io.resource.StringResource;
import lombok.EqualsAndHashCode;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class ForwardingCounterTest extends BaseCounterTest {

   @Override
   Counter<String> getCounter1() {
      return new FC<>(super.getCounter1());
   }

   @Override
   Counter<String> getCounter2() {
      return new FC<>(super.getCounter2());
   }

   @Override
   Counter<String> getCounter3() {
      return new FC<>(super.getCounter3());
   }

   @Override
   Counter<String> getEmptyCounter() {
      return new FC<>(super.getEmptyCounter());
   }

   @EqualsAndHashCode(callSuper = false)
   static class FC<T> extends ForwardingCounter<T> {
      private static final long serialVersionUID = 1L;
      final Counter<T> delegate;

      FC(Counter<T> delegate) {this.delegate = delegate;}

      @Override
      protected Counter<T> delegate() {
         return delegate;
      }
   }


   @Override
   public void csv() throws Exception {
      Counter<String> counter = getCounter2();
      Resource r = new StringResource();
      counter.writeCsv(r);
      Counter<String> fromCSV = new FC<>(Counters.readCsv(r, String.class));
      assertEquals(counter, fromCSV);
   }

   @Override
   public void json() throws Exception {
      Counter<String> counter = getCounter2();
      Resource r = new StringResource();
      counter.writeJson(r);
      Counter<String> fromJSON = new FC<>(Counters.readJson(r, String.class));
      assertEquals(counter, fromJSON);
   }

   @Override
   public void copy() throws Exception {
      Counter<String> counter = getCounter2();
      assertEquals(counter, new FC<>(counter.copy()));
   }


}
