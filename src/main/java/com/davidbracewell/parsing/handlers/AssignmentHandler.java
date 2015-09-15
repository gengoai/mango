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
import com.davidbracewell.parsing.expressions.AssignmentExpression;
import com.davidbracewell.parsing.expressions.Expression;

/**
 * A <code>InfixHandler</code> for assignment operations
 *
 * @author David B. Bracewell
 */
public class AssignmentHandler extends InfixHandler {

  /**
   * Default constructor
   *
   * @param precedence The precedence of the handler
   */
  public AssignmentHandler(int precedence) {
    super(precedence);
  }

  @Override
  public Expression parse(Parser parser, Expression left, ParserToken token) throws ParseException {
    Expression right = parser.next(precedence() - 1);
    if (right == null) {
      throw new ParseException("Assignment operator expects expression on right hand side of the operator, but found nothing");
    }
    return new AssignmentExpression(left.toString(), token, right);
  }

}//END OF AssignmentHandler
