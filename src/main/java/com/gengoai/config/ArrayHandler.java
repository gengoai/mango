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
 *
 */

package com.gengoai.config;

import com.gengoai.parsing.ExpressionIterator;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.ParserToken;
import com.gengoai.parsing.ParserTokenType;
import com.gengoai.parsing.expressions.Expression;
import com.gengoai.parsing.handlers.PrefixHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A handler to process an array of items with an optional separator between items.
 *
 * @author David B. Bracewell
 */
public class ArrayHandler implements PrefixHandler {
   private static final long serialVersionUID = 1L;
   private final ParserTokenType endOfArray;
   private final ParserTokenType separator;

   /**
    * Instantiates a new Array handler.
    *
    * @param endOfArray the end of array token type
    * @param separator  the separator token type (Optional)
    */
   public ArrayHandler(ParserTokenType endOfArray, ParserTokenType separator) {
      this.endOfArray = endOfArray;
      this.separator = separator;
   }

   @Override
   public Expression parse(ExpressionIterator expressionIterator, ParserToken token) throws ParseException {
      List<Expression> subExpressions = new ArrayList<>();
      while (!expressionIterator.tokenStream().lookAheadType(0).isInstance(endOfArray)) {
         Expression exp = expressionIterator.next();
         subExpressions.add(exp);
         if (!expressionIterator.tokenStream().lookAheadType(0).isInstance(endOfArray)) {
            if (separator != null) {
               expressionIterator.tokenStream().consume(separator);
            }
         }
      }
      expressionIterator.tokenStream().consume(endOfArray);
      return new ArrayExpression(token.type, subExpressions);
   }
}//END OF ArrayHandler
