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

import java.util.function.BiPredicate;

/**
 * @author David B. Bracewell
 */
public interface Comparison {

  static <T extends Comparable<T>> BiPredicate<T, T> greaterThan() {
    return (t1, t2) -> {
      if (t1 == null && t2 == null) {
        return false;
      } else if (t1 == null) {
        return false;
      } else if (t2 == null) {
        return true;
      }
      return t1.compareTo(t2) > 0;
    };
  }

  static <T extends Comparable<T>> BiPredicate<T, T> lessThan() {
    return (t1, t2) -> {
      if (t1 == null && t2 == null) {
        return false;
      } else if (t1 == null) {
        return true;
      } else if (t2 == null) {
        return false;
      }
      return t1.compareTo(t2) < 0;
    };
  }

  static <T extends Comparable<T>> BiPredicate<T, T> greaterThanEqual() {
    return (t1, t2) -> {
      if (t1 == null || t2 == null) {
        return true;
      }
      return t1.compareTo(t2) >= 0;
    };
  }

  static <T extends Comparable<T>> BiPredicate<T, T> lessThanEqual() {
    return (t1, t2) -> {
      if (t1 == null || t2 == null) {
        return true;
      }
      return t1.compareTo(t2) <= 0;
    };
  }


}//END OF Comparison
