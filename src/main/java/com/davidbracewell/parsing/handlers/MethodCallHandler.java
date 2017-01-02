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
import com.davidbracewell.parsing.ParserTokenType;
import com.davidbracewell.parsing.expressions.Expression;
import com.davidbracewell.parsing.expressions.MethodCallExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Creates {@link MethodCallExpression}s where the current token denotes the token type the signifies a method call
 * and the left token is the method name. The handler will capture expressions separated by the defined
 * <code>methodArgumentSeparator</code> until the <code>methodParamEnd</code> token type is reached.</p>
 *
 * @author David B. Bracewell
 */
public class MethodCallHandler extends InfixHandler {
   private static final long serialVersionUID = 1L;
   private final ParserTokenType methodParamEnd;
   private final ParserTokenType methodArgumentSeparator;

   /**
    * Default constructor
    *
    * @param precedence              The precedence of the handler
    * @param methodParamEnd          The token type indicating the end of the method call
    * @param methodArgumentSeparator The token type for the argument separator
    */
   public MethodCallHandler(int precedence, ParserTokenType methodParamEnd, ParserTokenType methodArgumentSeparator) {
      super(precedence);
      this.methodParamEnd = methodParamEnd;
      this.methodArgumentSeparator = methodArgumentSeparator;
   }

   @Override
   public Expression parse(ExpressionIterator expressionIterator, Expression left, ParserToken token) throws ParseException {
      List<Expression> args = new ArrayList<>();
      if (!expressionIterator.tokenStream().match(methodParamEnd)) {
         do {
            args.add(expressionIterator.next());
         } while (expressionIterator.tokenStream().match(methodArgumentSeparator));
         expressionIterator.tokenStream().consume(methodParamEnd);
      }
      return new MethodCallExpression(left.toString(), args, token.type);
   }

}
