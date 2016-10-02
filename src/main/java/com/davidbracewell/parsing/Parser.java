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

import com.davidbracewell.io.Resources;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.parsing.expressions.Expression;
import lombok.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>An implementation of a <a href="http://en.wikipedia.org/wiki/Pratt_parser">Pratt Parser</a> inspired by <a
 * href="http://journal.stuffwithstuff.com/2011/03/19/pratt-parsers-expression-parsing-made-easy/">Pratt Parsers:
 * Expression Parsing Made Easy</a>. An instance of a Parser is tied to a specific <code>Grammar</code> and
 * <code>TokenStream</code>. This implementation allows for operator precedence to be defined for each
 * <code>ParserHandler</code> and allows the precedence to be specified when retrieving the next expression.</p>
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
    * @param grammar the grammar to use for parsing
    * @param lexer   the lexer to use for tokenizing input
    */
   public Parser(Grammar grammar, Lexer lexer) {
      this.grammar = grammar;
      this.lexer = lexer;
   }

   /**
    * <p>Parses the given string returning an {@link ExpressionIterator} to iterate over the parsed expressions. Note
    * the parse is lazy and is done during the calls to {@link ExpressionIterator#next()}.</P>
    *
    * @param string the string to parse
    * @return the expression iterator that lazily parses the resource
    * @throws ParseException tSomething went wrong parsing
    */
   public ExpressionIterator parse(@NonNull String string) throws ParseException {
      return new ExpressionIterator(grammar, lexer.lex(string));
   }

   /**
    * <p>Parses the given resource returning an {@link ExpressionIterator} to iterate over the parsed expressions. Note
    * the parse is lazy and is done during the calls to {@link ExpressionIterator#next()}.</P>
    *
    * @param resource the resource to parse
    * @return the expression iterator that lazily parses the resource
    * @throws ParseException tSomething went wrong parsing
    */
   public ExpressionIterator parse(@NonNull Resource resource) throws ParseException {
      try {
         return new ExpressionIterator(grammar, lexer.lex(resource));
      } catch (IOException e) {
         throw new ParseException(e);
      }
   }

   /**
    * <p>Parses the given string and evaluates it with the given evaluator. Requires that the parse result in a single
    * expression.</p>
    *
    * @param <O>       the return type of the evaluator
    * @param string    the string to evaluate
    * @param evaluator the evaluator to use for transforming expressions
    * @return the single return values from the evaluator
    * @throws ParseException Something went wrong parsing
    */
   public <O> O evaluate(@NonNull String string, @NonNull Evaluator<? extends O> evaluator) throws ParseException {
      return evaluate(Resources.fromString(string), evaluator);
   }

   /**
    * <p>Parses the given resource and evaluates it with the given evaluator. Requires that the parse result in a single
    * expression.</p>
    *
    * @param <O>       the return type of the evaluator
    * @param resource  the resource to evaluate
    * @param evaluator the evaluator to use for transforming expressions
    * @return the single return values from the evaluator
    * @throws ParseException Something went wrong parsing
    */
   public <O> O evaluate(@NonNull Resource resource, @NonNull Evaluator<? extends O> evaluator) throws ParseException {
      ExpressionIterator iterator = parse(resource);
      Expression expression = iterator.next();
      if (iterator.hasNext()) {
         throw new ParseException("Did not fully parse token stream");
      }
      try {
         return evaluator.eval(expression);
      } catch (Exception e) {
         throw new ParseException(e);
      }
   }

   /**
    * <p>Parses the given string and evaluates it with the given evaluator.</p>
    *
    * @param <O>       the return type of the evaluator
    * @param string    the string to evaluate
    * @param evaluator the evaluator to use for transforming expressions
    * @return A list of objects relating to the transformation of expressions by the given evaluator.
    * @throws ParseException Something went wrong parsing
    */
   public <O> List<O> evaluateAll(@NonNull String string, @NonNull Evaluator<? extends O> evaluator) throws ParseException {
      return evaluateAll(Resources.fromString(string), evaluator);
   }

   /**
    * <p>Parses the given resource and evaluates it with the given evaluator.</p>
    *
    * @param <O>       the return type of the evaluator
    * @param resource  the resource to evaluate
    * @param evaluator the evaluator to use for transforming expressions
    * @return A list of objects relating to the transformation of expressions by the given evaluator.
    * @throws ParseException Something went wrong parsing
    */
   public <O> List<O> evaluateAll(@NonNull Resource resource, @NonNull Evaluator<? extends O> evaluator) throws ParseException {
      ExpressionIterator iterator = parse(resource);
      List<O> evaluationResults = new ArrayList<>();
      while (iterator.hasNext()) {
         try {
            evaluationResults.add(evaluator.eval(iterator.next()));
         } catch (Exception e) {
            throw new ParseException(e);
         }
      }
      return evaluationResults;
   }

}//END OF Parser
