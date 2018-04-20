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
import com.gengoai.parsing.ParserTokenType;
import com.gengoai.parsing.expressions.CommentExpression;
import com.gengoai.parsing.expressions.Expression;
import com.gengoai.string.StringUtils;

import java.util.List;

/**
 * <p>Creates a comment expression where the everything from the comment token to the end of line (specified via the
 * <code>endOfLine</code> token type) is found.</p>
 *
 * @author David B. Bracewell
 */
public class CommentHandler extends PrefixHandler {
   private static final long serialVersionUID = 1L;
   private final ParserTokenType endOfLine;

   /**
    * Instantiates a new Comment handler.
    *
    * @param endOfLine the token type represent the end of line (where the comment stops).
    */
   public CommentHandler(ParserTokenType endOfLine) {
      this.endOfLine = endOfLine;
   }

   @Override
   public Expression parse(ExpressionIterator expressionIterator, ParserToken token) throws ParseException {
      List<ParserToken> tokens = expressionIterator.tokenStream().consumeUntil(endOfLine);
      if (expressionIterator.tokenStream().lookAheadType(0) != null) {
         expressionIterator.tokenStream().consume(endOfLine);
      }
      return new CommentExpression(token.type,
                                   token.text + tokens.stream()
                                                      .map(ParserToken::getText)
                                                      .reduce(String::concat)
                                                      .orElse(StringUtils.EMPTY)
      );
   }

}//END OF CommentHandler
