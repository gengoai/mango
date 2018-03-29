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

package com.gengoai.mango.parsing.expressions;

import com.gengoai.mango.parsing.ParserTokenType;
import lombok.NonNull;

/**
 * <p>Represents a single expression from a parse.</p>
 *
 * @author David B. Bracewell
 */
public abstract class Expression {

  private final ParserTokenType type;

  /**
   * Default Constructor
   *
   * @param type The type of token that dominates the expression
   */
  public Expression(ParserTokenType type) {
    this.type = type;
  }

  /**
   * Determines if the type matches
   *
   * @param rhs The type to check
   * @return true if a match, false otherwise
   */
  public final boolean match(ParserTokenType rhs) {
    return type.equals(rhs);
  }

  /**
   * Tries to convert the expression into a specific implementation.
   *
   * @param <T>   The implementation to convert to
   * @param clazz Class information for the implementation to convert to
   * @return The expression as the given implementation or null if could not convert.
   */
  public final <T extends Expression> T as(@NonNull Class<T> clazz) {
    return isInstance(clazz) ? clazz.cast(this) : null;
  }

  /**
   * Is instance.
   *
   * @param <T>   the type parameter
   * @param clazz the clazz
   * @return the boolean
   */
  public final <T extends Expression> boolean isInstance(@NonNull Class<T> clazz) {
    return clazz.isInstance(this);
  }

  /**
   * Gets token type.
   *
   * @return The dominate token type for the expression
   */
  public final ParserTokenType getTokenType() {
    return type;
  }

  public final <T extends Expression> boolean match(@NonNull Class<T> clazz, @NonNull ParserTokenType type) {
    return isInstance(clazz) && match(type);
  }


}//END OF Expression
