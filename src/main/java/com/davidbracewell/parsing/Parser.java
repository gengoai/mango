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

import com.davidbracewell.parsing.expressions.Expression;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>An implementation of a <a href="http://en.wikipedia.org/wiki/Pratt_parser">Pratt Parser</a> inspired by <a
 * href="http://journal.stuffwithstuff.com/2011/03/19/pratt-parsers-expression-parsing-made-easy/"></a>. An instance of
 * a Parser is tied to a specific <code>Grammar</code> and <code>TokenStream</code>. This implementation allows for
 * operator precedence to be defined for each <code>ParserHandler</code> and allows the precedence to be specified when
 * retrieving the next expression.</p>
 *
 * @author David B. Bracewell
 */
public class Parser {

  private final Grammar grammar;
  private final ParserTokenStream tokenStream;

  /**
   * Constructs a parser for the given token stream
   *
   * @param grammar     the grammar
   * @param tokenStream The stream of tokens to parse
   */
  public Parser(@NonNull Grammar grammar, @NonNull ParserTokenStream tokenStream) {
    this.grammar = grammar;
    this.tokenStream = tokenStream;
  }

  /**
   * Determine if there is more to parse or not
   *
   * @return True if there are more expressions to parse, False if at end of stream
   */
  public boolean hasNext() {
    return tokenStream.lookAhead(0) != null;
  }

  /**
   * Parses the next expression with the lowest precedence (0)
   *
   * @return The next expression
   * @throws ParseException An error occurred parsing
   */
  public Expression next() throws ParseException {
    return next(0);
  }

  /**
   * Token stream.
   *
   * @return The token stream used by the parser
   */
  public final ParserTokenStream tokenStream() {
    return tokenStream;
  }

  /**
   * Parses the entire token stream until the end
   *
   * @return A list of expressions parsed from the underlying stream
   * @throws ParseException Something went wrong parsing.
   */
  public List<Expression> parse() throws ParseException {
    List<Expression> parseTree = new ArrayList<>();
    while (hasNext()) {
      parseTree.add(next());
    }
    return parseTree;
  }

  /**
   * Parses the next expression with a given precedence.
   *
   * @param precedence The precedence to use in parsing
   * @return The next expression or null if at the end of the token stream
   * @throws ParseException An error occurred parsing
   */
  public Expression next(int precedence) throws ParseException {
    ParserToken token;
    Expression result;
    do {
      token = tokenStream.consume();
      if (token == null) {
        return null;
      }
      result = grammar.parse(this, token);
    } while (result == null);

    while (precedence < grammar.precedence(tokenStream.lookAhead(0))) {
      token = tokenStream.consume();
      result = grammar.parse(this, result, token);
    }
    return result;
  }

}//END OF PrattParser
