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

import com.davidbracewell.parsing.ParserToken;
import com.davidbracewell.parsing.ParserTokenType;
import com.google.common.base.Preconditions;

/**
 * An <code>Expression</code> for assignment operations.
 *
 * @author David B. Bracewell
 */
public class AssignmentExpression extends Expression {

  /**
   * The assignment operator
   */
  public final String operator;
  /**
   * The expression whose value is being assigned to the variable
   */
  public final Expression right;
  /**
   * The variable receiving the assignment
   */
  public final String variableName;

  /**
   * Default Constructor
   *
   * @param variableName The name of the variable that is being assigned to
   * @param operator     The assignment operator
   * @param right        The expression which the variable should be assigned
   */
  public AssignmentExpression(String variableName, ParserToken operator, Expression right) {
    super(Preconditions.checkNotNull(operator).type);
    this.variableName = Preconditions.checkNotNull(variableName);
    this.operator = operator.text;
    this.right = Preconditions.checkNotNull(right);
  }

  /**
   * Default Constructor
   *
   * @param variableName The name of the variable that is being assigned to
   * @param operator     The assignment operator
   * @param right        The expression which the variable should be assigned
   * @param type         The tyoke type of the assignemt
   */
  public AssignmentExpression(String variableName, String operator, Expression right, ParserTokenType type) {
    super(type);
    this.variableName = Preconditions.checkNotNull(variableName);
    this.operator = operator;
    this.right = Preconditions.checkNotNull(right);
  }

  @Override
  public String toString() {
    return "(" + variableName + " " + operator + " " + right.toString() + ")";
  }


}//END OF AssignmentExpression
