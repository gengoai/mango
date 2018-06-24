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

package com.gengoai.function;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * Version of Predicate that is serializable
 *
 * @param <T> Functional parameter
 */
@FunctionalInterface
public interface SerializablePredicate<T> extends Predicate<T>, Serializable {

   @Override
   default SerializablePredicate<T> negate() {
      return t -> !this.test(t);
   }

   @Override
   default SerializablePredicate<T> or( Predicate<? super T> other) {
      return t -> this.test(t) || other.test(t);
   }

   @Override
   default SerializablePredicate<T> and( Predicate<? super T> other) {
      return t -> this.test(t) && other.test(t);
   }

   default SerializablePredicate<T> or( SerializablePredicate<? super T> other) {
      return t -> this.test(t) || other.test(t);
   }

   default SerializablePredicate<T> and( SerializablePredicate<? super T> other) {
      return t -> this.test(t) && other.test(t);
   }

}//END OF SerializablePredicate
