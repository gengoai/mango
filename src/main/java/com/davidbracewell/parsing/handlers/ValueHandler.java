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
import com.davidbracewell.parsing.expressions.ValueExpression;

/**
 * <p>Creates {@link ValueExpression}s which simply wrap the type and text of the current token.</p>
 *
 * @author David B. Bracewell
 */
public class ValueHandler extends PrefixHandler {
   private static final long serialVersionUID = 1L;

   @Override
   public Expression parse(ExpressionIterator expressionIterator, ParserToken token) throws ParseException {
      return new ValueExpression(token.text, token.type);
   }

}//END OF ValueHandler

