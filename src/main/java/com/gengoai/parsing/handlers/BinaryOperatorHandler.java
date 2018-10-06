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

package com.gengoai.parsing.handlers;


import com.gengoai.parsing.ExpressionIterator;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.ParserToken;
import com.gengoai.parsing.expressions.BinaryOperatorExpression;
import com.gengoai.parsing.expressions.Expression;

/**
 * <p>Creates {@link BinaryOperatorExpression}s where the left and right expressions are being operated on by the
 * operator represented by the current token.</p>
 *
 * @author David B. Bracewell
 */
public class BinaryOperatorHandler extends InfixHandler {
   private static final long serialVersionUID = 1L;
   private final boolean rightAssociative;

   /**
    * Default constructor
    *
    * @param precedence       The precedence of the handler
    * @param rightAssociative true if the handler is right associative
    */
   public BinaryOperatorHandler(int precedence, boolean rightAssociative) {
      super(precedence);
      this.rightAssociative = rightAssociative;
   }

   @Override
   public Expression parse(ExpressionIterator expressionIterator, Expression left, ParserToken token) throws ParseException {
      return new BinaryOperatorExpression(left,
                                          token,
                                          expressionIterator.next(precedence() - (rightAssociative ? 1 : 0)));
   }


}//END OF BinaryOperatorHandler
