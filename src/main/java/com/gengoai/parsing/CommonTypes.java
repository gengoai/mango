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

import static com.gengoai.parsing.ParserTokenType.tokenType;
import static com.gengoai.parsing.TokenDef.define;

/**
 * A enum of common Parser Token Types.
 *
 * @author David B. Bracewell
 */
public final class CommonTypes {

   public static final TokenDef NUMBER = define(tokenType("NUMBER"), "\\d*(\\.\\d+)?");
   public static final TokenDef WORD = define(tokenType("WORD"), "\\p{L}+");

   public static final TokenDef OPENPARENS = define(tokenType("OPENPARENS"), "\\(");

   public static final TokenDef CLOSEPARENS = define(tokenType("CLOSEPARENS"), "\\)");

   public static final TokenDef OPENBRACKET = define(tokenType("OPENBRACKET"), "\\[");

   public static final TokenDef CLOSEBRACKET = define(tokenType("CLOSEBRACKET"), "\\]");

   public static final TokenDef OPENBRACE = define(tokenType("OPENBRACE"), "\\{");

   public static final TokenDef CLOSEBRACE = define(tokenType("CLOSEBRACE"), "\\}");

   public static final TokenDef PERIOD = define(tokenType("PERIOD"), "\\.");

   public static final TokenDef PLUS = define(tokenType("PLUS"), "\\+");

   public static final TokenDef MINUS = define(tokenType("MINUS"), "\\-");

   public static final TokenDef MULTIPLY = define(tokenType("MULTIPLY"), "\\*");

   public static final TokenDef DIVIDE = define(tokenType("DIVIDE"), "/");

   public static final TokenDef EXCLAMATION = define(tokenType("EXCLAMATION"), "\\!");

   public static final TokenDef POUND = define(tokenType("POUND"), "\\#");

   public static final TokenDef COMMA = define(tokenType("COMMA"), ",");

   public static final TokenDef EQUALS = define(tokenType("EQUALS"), "=");

   public static final TokenDef DOUBLEQUOTE = define(tokenType("DOUBLEQUOTE"), "\"");

   public static final TokenDef AMPERSAND = define(tokenType("AMPERSAND"), "\\&");

   public static final TokenDef DOLLAR = define(tokenType("DOLLAR"), "\\$");

   public static final TokenDef AT = define(tokenType("AT"), "@");

   public static final TokenDef CARROT = define(tokenType("CARROT"), "\\^");

   public static final TokenDef COLON = define(tokenType("COLON"), ":");

   public static final TokenDef SEMICOLON = define(tokenType("SEMICOLON"), ";");

   public static final TokenDef QUESTION = define(tokenType("QUESTION"), "\\?");

   public static final TokenDef BACKSLASH = define(tokenType("BACKSLASH"), "\\\\");

   public static final TokenDef FORWARDSLASH = define(tokenType("FORWARDSLASH"), "/");

   public static final TokenDef SINGLEQUOTE = define(tokenType("SINGLEQUOTE"), "'");

   public static final TokenDef NEWLINE = define(tokenType("NEWLINE"), "\r?\n");

   public static final TokenDef WHITESPACE = define(tokenType("WHITESPACE"), "\\p{Zs}");

   public static final TokenDef TILDE = define(tokenType("TILDE"), "\\~");

   public static final TokenDef PIPE = define(tokenType("PIPE"), "\\|");


}//END OF CommonTypes
