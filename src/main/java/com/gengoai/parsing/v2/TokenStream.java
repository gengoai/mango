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
 *
 */

package com.gengoai.parsing.v2;

import com.gengoai.StringTag;
import com.gengoai.Tag;
import com.gengoai.string.Strings;

import java.io.Serializable;

/**
 * The interface Token stream.
 *
 * @author David B. Bracewell
 */
public interface TokenStream extends Serializable {
   /**
    * The constant EOF.
    */
   Tag EOF = new StringTag("~~~EOF~~~");
   /**
    * The constant EOF_TOKEN.
    */
   ParserToken EOF_TOKEN = new ParserToken(EOF, Strings.EMPTY, -1, -1);

   /**
    * Token parser token.
    *
    * @return the parser token
    */
   ParserToken token();

   /**
    * Consume parser token.
    *
    * @return the parser token
    */
   ParserToken consume();

   /**
    * Peek parser token.
    *
    * @return the parser token
    */
   ParserToken peek();

   /**
    * Consume parser token.
    *
    * @param target the target
    * @return the parser token
    */
   default ParserToken consume(Tag target) {
      ParserToken token = consume();
      if (!token.isInstance(target)) {
         throw new IllegalArgumentException(
            "Parsing Error: consumed token of type " + token.getType() + ", but was expecting " + target);
      }
      return token;
   }


   /**
    * Has next boolean.
    *
    * @return the boolean
    */
   default boolean hasNext() {
      return !peek().isInstance(EOF);
   }

}//END OF TokenStream
