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

import com.davidbracewell.function.CheckedFunction;
import com.davidbracewell.function.SerializablePredicate;
import com.davidbracewell.function.Unchecked;
import lombok.Builder;
import lombok.Singular;

import java.io.Serializable;
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
@Builder
public class Switch<T, R> implements Serializable {
  private static final long serialVersionUID = 1L;

  @Singular
  private Map<SerializablePredicate<? super T>, CheckedFunction<? super T, ? extends R>> caseStmts = new LinkedHashMap<>();
  private CheckedFunction<? super T, ? extends R> defaultStmt;

  /**
   * Instantiates a new Switch.
   *
   * @param caseStmts   the case stmts
   * @param defaultStmt the default stmt
   */
  @java.beans.ConstructorProperties({"caseStmts", "defaultStmt"})
  Switch(Map<SerializablePredicate<? super T>, CheckedFunction<? super T, ? extends R>> caseStmts, CheckedFunction<? super T, ? extends R> defaultStmt) {
    this.caseStmts = caseStmts;
    this.defaultStmt = defaultStmt;
  }

  /**
   * Switch on r.
   *
   * @param argument the argument
   * @return the r
   */
  public R switchOn(T argument) {
    for (SerializablePredicate<? super T> p : caseStmts.keySet()) {
      if (p.test(argument)) {
        return Unchecked.function(caseStmts.get(p)).apply(argument);
      }
    }
    if (defaultStmt != null) {
      return Unchecked.function(defaultStmt).apply(argument);
    }
    return null;
  }


}//END OF Switch
