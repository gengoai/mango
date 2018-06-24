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

import com.gengoai.io.Resources;
import com.gengoai.io.resource.Resource;

import java.io.IOException;

/**
 * <p>A Lexer tokenizes a string or resource into tokens.</p>
 *
 * @author David B. Bracewell
 */
public interface Lexer {

   /**
    * Tokenizes the input string into tokens
    *
    * @param input the input to tokenize
    * @return A token stream wrapping the tokenization results
    */
   default ParserTokenStream lex(final String input) {
      try {
         return lex(Resources.fromString(input));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Reads from the given resource and tokenizes it into tokens
    *
    * @param input the resource to read and tokenize
    * @return A token stream wrapping the tokenization results
    * @throws IOException Something went wrong reading from the input resource
    */
   ParserTokenStream lex(final Resource input) throws IOException;

}//END OF Lexer
