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

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.CheckedFunction;
import com.davidbracewell.function.SerializablePredicate;
import com.google.common.base.Throwables;
import lombok.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts a value to another based on a series of predicates. In essence allows for <code>switch</code> statements in
 * the form of complex predicate -> function pairs to be performed on any data type.
 *
 * @param <T> the type parameter being switched on
 * @param <R> the type parameter returned from the switch operation
 * @author David B. Bracewell
 */
public class Switch<T, R> implements Serializable {
  private static final long serialVersionUID = 1L;

  private Map<SerializablePredicate<? super T>, CheckedFunction<? super T, ? extends R>> caseStmts = new LinkedHashMap<>();
  private CheckedFunction<? super T, ? extends R> defaultStmt;

  /**
   * Instantiates a new Switch.
   *
   * @param caseStmts   the case stmts
   * @param defaultStmt the default stmt
   */
  private Switch(Map<SerializablePredicate<? super T>, CheckedFunction<? super T, ? extends R>> caseStmts, CheckedFunction<? super T, ? extends R> defaultStmt) {
    this.caseStmts = caseStmts;
    this.defaultStmt = defaultStmt;
  }

  public static <T, R> Builder<T, R> builder() {
    return new Builder<>();
  }

  /**
   * Switch on r.
   *
   * @param argument the argument
   * @return the r
   */
  public R switchOn(T argument) throws Exception {
    for (SerializablePredicate<? super T> p : caseStmts.keySet()) {
      if (p.test(argument)) {
        try {
          return caseStmts.get(p).apply(argument);
        } catch (Throwable throwable) {
          throw toException(Throwables.getRootCause(throwable));
        }
      }
    }
    if (defaultStmt != null) {
      try {
        return defaultStmt.apply(argument);
      } catch (Throwable throwable) {
        throw toException(Throwables.getRootCause(throwable));
      }
    }
    return null;
  }

  private Exception toException(Throwable throwable) {
    if (throwable instanceof Exception) {
      return Cast.as(throwable);
    }
    return new Exception(throwable);
  }

  public static class Builder<T, R> {
    private final Map<SerializablePredicate<? super T>, CheckedFunction<? super T, ? extends R>> caseStmts = new HashMap<>();
    private CheckedFunction<? super T, ? extends R> defaultStmt = null;

    public Builder<T, R> defaultStatement(CheckedFunction<? super T, ? extends R> defaultStmt) {
      this.defaultStmt = defaultStmt;
      return this;
    }

    public Builder<T, R> caseStmt(@NonNull SerializablePredicate<? super T> predicate, @NonNull CheckedFunction<? super T, ? extends R> function) {
      this.caseStmts.put(predicate, function);
      return this;
    }

    public <V> Builder<T, R> caseStmt(@NonNull SerializablePredicate<? super T> predicate, @NonNull CheckedFunction<? super T, V> mapper, @NonNull CheckedFunction<? super V, ? extends R> function) {
      this.caseStmts.put(predicate, mapper.andThen(function));
      return this;
    }

    public Switch<T, R> build() {
      return new Switch<>(caseStmts, defaultStmt);
    }

  }


}//END OF Switch
