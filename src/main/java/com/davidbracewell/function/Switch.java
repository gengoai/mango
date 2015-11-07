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

package com.davidbracewell.function;

import lombok.Builder;
import lombok.Singular;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author David B. Bracewell
 */
@Builder
public class Switch<T, R> implements Serializable {
  private static final long serialVersionUID = 1L;

  @Singular
  private Map<SerializablePredicate<? super T>, SerializableFunction<? super T, ? extends R>> caseStmts = new LinkedHashMap<>();
  private SerializableFunction<? super T, ? extends R> defaultStmt;

  @java.beans.ConstructorProperties({"caseStmts", "defaultStmt"})
  Switch(Map<SerializablePredicate<? super T>, SerializableFunction<? super T, ? extends R>> caseStmts, SerializableFunction<? super T, ? extends R> defaultStmt) {
    this.caseStmts = caseStmts;
    this.defaultStmt = defaultStmt;
  }

  public R switchOn(T argument) {
    for (SerializablePredicate<? super T> p : caseStmts.keySet()) {
      if (p.test(argument)) {
        return caseStmts.get(p).apply(argument);
      }
    }
    if (defaultStmt != null) {
      return defaultStmt.apply(argument);
    }
    return null;
  }

}//END OF Switch
