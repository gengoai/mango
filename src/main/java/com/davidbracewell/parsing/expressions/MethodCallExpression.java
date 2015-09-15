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

package com.davidbracewell.parsing.expressions;

import com.davidbracewell.parsing.ParserTokenType;

import java.util.Collections;
import java.util.List;

/**
 * An <code>Expression</code> representing a method call and its arguments
 *
 * @author David B. Bracewell
 */

public class MethodCallExpression extends Expression {

  /**
   * The method name
   */
  public final String methodName;

  /**
   * Expressions representing the arguments to the method call
   */
  public final List<Expression> arguments;

  /**
   * Default Constructor
   *
   * @param methodName The method name
   * @param arguments  The arguments to the method
   * @param type       The type of the method
   */
  public MethodCallExpression(String methodName, List<Expression> arguments, ParserTokenType type) {
    super(type);
    this.methodName = methodName;
    this.arguments = Collections.unmodifiableList(arguments);
  }

  @Override
  public String toString() {
    return "(" + methodName + arguments + ")";
  }

}//END OF MethodCallExpression
