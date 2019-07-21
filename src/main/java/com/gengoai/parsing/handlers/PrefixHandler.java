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
import com.gengoai.parsing.expressions.Expression;

import java.io.Serializable;

/**
 * <p>Abstract base class for prefix handlers.</p>
 *
 * @author David B. Bracewell
 */
public interface PrefixHandler extends ParserHandler, Serializable {

   /**
    * Constructs an expression from the current token and the parser.
    *
    * @param expressionIterator The parser
    * @param token              The token that caused the handler to be invoked
    * @return An expression representing the parse
    * @throws ParseException An error occurred parsing
    */
   Expression parse(ExpressionIterator expressionIterator, ParserToken token) throws ParseException;


   @Override
   default int precedence() {
      return 0;
   }

}//END OF PrefixHandler
