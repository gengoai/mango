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

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * An implementation of a lexer that uses regular expressions to tokenize a String into a
 * <code>ParserTokenStream</code>.
 *
 * @author David B. Bracewell
 */
public class RegularExpressionLexer extends Lexer {

  private final Set<ParserTokenType> types;
  private final Pattern lexer;

  /**
   * Default Constructor
   *
   * @param lexicalPatterns The lexical patterns to capture
   */
  public RegularExpressionLexer(@NonNull Collection<Tuple2<ParserTokenType, String>> lexicalPatterns) {
    Preconditions.checkArgument(!lexicalPatterns.isEmpty());
    types = Sets.newHashSet();
    StringBuilder pattern = new StringBuilder();
    for (Tuple2<ParserTokenType, String> Tuple2 : lexicalPatterns) {
      types.add(Tuple2.getKey());
      pattern.append(String.format("|(?<%s>%s)", Tuple2.getKey().toString(), Tuple2.getValue()));
    }
    this.lexer = Pattern.compile("(" + pattern.toString().substring(1) + ")", Pattern.MULTILINE | Pattern.DOTALL);
  }

  @Override
  public ParserTokenStream lex(final Resource input) throws IOException {
    return new ParserTokenStream(new MatchIterator(input.readToString()));
  }


  private class MatchIterator implements Iterator<ParserToken> {
    final Matcher matcher;
    final String input;
    Boolean hasNext;

    private MatchIterator(String input) {
      this.matcher = lexer.matcher(input);
      this.hasNext = matcher.find();
      this.input = input;
    }


    @Override
    public boolean hasNext() {
      if (hasNext == null) {
        hasNext = matcher.find();
      }
      return hasNext;
    }

    @Override
    public ParserToken next() {
      hasNext();
      if (!hasNext) {
        throw new NoSuchElementException();
      }
      hasNext = null;
      ParserToken token = null;
      for (ParserTokenType type : types) {
        if (matcher.group(type.toString()) != null) {
          List<String> groups = Lists.newArrayList();
          for (int i = 2; i < matcher.groupCount(); i++) {
            if (matcher.group(i) != null && !matcher.group(i).equals(matcher.group())) {
              groups.add(matcher.group(i));
            }
          }
          token = new ParserToken(matcher.group(), type);
          break;
        }
      }
      if (token == null) {
        throw new IllegalStateException("Error in parsing {" + input + "}");
      }

      return token;
    }

  }

  /**
   * @return A Builder to create a lexer
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * A builder for constructing Lexers
   *
   * @author David B. Bracewell
   */
  public static class Builder {

    private final Set<Tuple2<ParserTokenType, String>> lexicalItems = Sets.newLinkedHashSet();

    /**
     * Adds a pattern to capture a given type
     *
     * @param type    The token type
     * @param pattern The pattern that captures the given token type (Overrides the pattern on the TokenType)
     * @return The LexerBuilder
     */
    public Builder add(ParserTokenType type, String pattern) {
      lexicalItems.add(Tuple2.of(type, pattern));
      return this;
    }

    /**
     * Adds a pattern to capture a given type
     *
     * @param type The token type
     * @return The LexerBuilder
     */
    public Builder add(ParserTokenType type) {
      Preconditions.checkArgument(type instanceof HasLexicalPattern, "Type must define its own lexical pattern");
      lexicalItems.add(Tuple2.of(type, Cast.<HasLexicalPattern>as(type).lexicalPattern()));
      return this;
    }


    /**
     * @return A lexer
     */
    public RegularExpressionLexer build() {
      return new RegularExpressionLexer(lexicalItems);
    }


  }//END OF Lexer$Builder


}//END OF Lexer
