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

package com.gengoai.parsing;

import com.gengoai.Re;
import com.gengoai.Regex;


/**
 * A enum of common Parser Token Types.
 *
 * @author David B. Bracewell
 */
public enum CommonTypes implements ParserTokenType, HasLexicalPattern {
  NUMBER(
     Re.seq(
        Re.NUMBER.plus(),
        Re.nmGroup(Re.re(","), Re.NUMBER.nTimes(3)).star(),
        Re.nmGroup(Re.quote("."), Re.NUMBER.plus()).star()
           )
  ),
  WORD("\\p{L}+"),
  OPENPARENS("\\("),
  CLOSEPARENS("\\)"),
  OPENBRACKET("\\["),
  CLOSEBRACKET("\\]"),
  OPENBRACE("\\{"),
  CLOSEBRACE("\\}"),
  PERIOD("\\."),
  PLUS("\\+"),
  MINUS("\\-"),
  MULTIPLY("\\*"),
  DIVIDE("/"),
  EXCLAMATION("\\!"),
  POUND("\\#"),
  COMMA(","),
  EQUALS("="),
  DOUBLEQUOTE("\""),
  AMPERSAND("\\&"),
  DOLLAR("\\$"),
  AT("@"),
  CARROT("\\^"),
  COLON(":"),
  SEMICOLON(";"),
  QUESTION("\\?"),
  BACKSLASH("\\\\"),
  FORWARDSLASH("/"),
  SINGLEQUOTE("'"),
  NEWLINE("\r?\n"),
  WHITESPACE("\\p{Zs}"),
  TILDE("\\~"),
  PIPE("\\|");

  private final String pattern;

  CommonTypes(Regex pattern) {
    this.pattern = pattern.toString();
  }

  CommonTypes(String pattern) {
    this.pattern = pattern;
  }

  @Override
  public String lexicalPattern() {
    return pattern;
  }

}//END OF CommonTypes
