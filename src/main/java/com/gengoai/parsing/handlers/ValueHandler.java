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


import com.gengoai.function.SerializableFunction;
import com.gengoai.parsing.ExpressionIterator;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.ParserToken;
import com.gengoai.parsing.expressions.Expression;
import com.gengoai.parsing.expressions.StringValueExpression;
import com.gengoai.parsing.expressions.ValueExpression;

/**
 * <p>Creates {@link StringValueExpression}s which simply wrap the type and text of the current token.</p>
 *
 * @author David B. Bracewell
 */
public class ValueHandler implements PrefixHandler {
   private static final long serialVersionUID = 1L;
   private final SerializableFunction<ParserToken, ValueExpression> expressionSupplier;

   public ValueHandler(SerializableFunction<ParserToken, ValueExpression> expressionSupplier) {
      this.expressionSupplier = expressionSupplier;
   }

   public ValueHandler() {
      this(token -> new StringValueExpression(token.text, token.type));
   }

   @Override
   public Expression parse(ExpressionIterator expressionIterator, ParserToken token) throws ParseException {
      return expressionSupplier.apply(token);
   }

}//END OF ValueHandler

