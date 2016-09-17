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

package com.davidbracewell.parsing;

import com.davidbracewell.io.resource.Resource;
import lombok.NonNull;

import java.io.IOException;
import java.io.Serializable;

/**
 * <p>An implementation of a <a href="http://en.wikipedia.org/wiki/Pratt_parser">Pratt Parser</a> inspired by <a
 * href="http://journal.stuffwithstuff.com/2011/03/19/pratt-parsers-expression-parsing-made-easy/"></a>. An instance of
 * a Parser is tied to a specific <code>Grammar</code> and <code>TokenStream</code>. This implementation allows for
 * operator precedence to be defined for each <code>ParserHandler</code> and allows the precedence to be specified when
 * retrieving the next expression.</p>
 *
 * @author David B. Bracewell
 */
public class Parser implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Grammar grammar;
   private final Lexer lexer;

   /**
    * Instantiates a new Parser.
    *
    * @param grammar the grammar
    * @param lexer   the lexer
    */
   public Parser(Grammar grammar, Lexer lexer) {
      this.grammar = grammar;
      this.lexer = lexer;
   }

   /**
    * Parse expression iterator.
    *
    * @param string the string
    * @return the expression iterator
    */
   public ExpressionIterator parse(@NonNull String string) {
      return new ExpressionIterator(grammar, lexer.lex(string));
   }

   /**
    * Parse expression iterator.
    *
    * @param resource the resource
    * @return the expression iterator
    * @throws ParseException the io exception
    */
   public ExpressionIterator parse(@NonNull Resource resource) throws ParseException {
      try {
         return new ExpressionIterator(grammar, lexer.lex(resource));
      } catch (IOException e) {
         throw new ParseException(e);
      }
   }

}//END OF Parser
