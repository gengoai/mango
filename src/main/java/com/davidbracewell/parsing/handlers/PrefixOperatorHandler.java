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


import com.davidbracewell.parsing.ExpressionIterator;
import com.davidbracewell.parsing.ParseException;
import com.davidbracewell.parsing.ParserToken;
import com.davidbracewell.parsing.expressions.Expression;
import com.davidbracewell.parsing.expressions.PrefixOperatorExpression;

/**
 * <p>Creates {@link PrefixOperatorExpression}s where the current token is the operator and the next expression is what the
 * operator is operating on.</p>
 *
 * @author David B. Bracewell
 */
public class PrefixOperatorHandler extends PrefixHandler {
   private static final long serialVersionUID = 1L;
   private final Class<? extends Expression> expectedRightHandType;


   /**
    * Default constructor
    */
   public PrefixOperatorHandler() {
      this(null);
   }

   /**
    * Default constructor
    *
    * @param expectedRightHandType the expected right hand type
    */
   public PrefixOperatorHandler(Class<? extends Expression> expectedRightHandType) {
      this.expectedRightHandType = expectedRightHandType;
   }

   @Override
   public Expression parse(ExpressionIterator expressionIterator, ParserToken token) throws ParseException {
      Expression right = expressionIterator.next(precedence());
      if (expectedRightHandType != null && (right == null || right.as(expectedRightHandType) == null)) {
         throw new ParseException("Expecting the right hand argument to be of type" + expectedRightHandType + " but got " + (right == null
                                                                                                                             ? "null"
                                                                                                                             : right
                                                                                                                                  .getClass()));
      }
      return new PrefixOperatorExpression(token, right);
   }

}//END OF PrefixOperatorHandler
