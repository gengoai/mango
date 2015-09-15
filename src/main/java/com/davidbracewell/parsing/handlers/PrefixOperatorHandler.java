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

package com.davidbracewell.parsing.handlers;


import com.davidbracewell.parsing.ParseException;
import com.davidbracewell.parsing.Parser;
import com.davidbracewell.parsing.ParserToken;
import com.davidbracewell.parsing.expressions.Expression;
import com.davidbracewell.parsing.expressions.PrefixExpression;

/**
 * A <code>PrefixHandler</code> for prefix operators.
 *
 * @author David B. Bracewell
 */
public class PrefixOperatorHandler extends PrefixHandler {

  private final Class<? extends Expression> expectedRightHandType;


  /**
   * Default constructor
   *
   * @param precedence The precedence of the handler
   */
  public PrefixOperatorHandler(int precedence) {
    this(precedence, null);
  }

  /**
   * Default constructor
   *
   * @param precedence            The precedence of the handler
   * @param expectedRightHandType the expected right hand type
   */
  public PrefixOperatorHandler(int precedence, Class<? extends Expression> expectedRightHandType) {
    super(precedence);
    this.expectedRightHandType = expectedRightHandType;
  }

  @Override
  public Expression parse(Parser parser, ParserToken token) throws ParseException {
    Expression right = parser.next(precedence());
    if (expectedRightHandType != null && (right == null || right.as(expectedRightHandType) == null)) {
      throw new ParseException("Expecting the right hand argument to be of type" + expectedRightHandType + " but got " + (right == null ? "null" : right.getClass()));
    }
    return new PrefixExpression(token, right);
  }

}//END OF PrefixOperatorHandler
