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

package com.gengoai.parsing.expressions;


import com.gengoai.parsing.ParserToken;
import com.gengoai.parsing.ParserTokenType;

/**
 * An <code>Expression</code> representing a simple value.
 *
 * @author David B. Bracewell
 */
public class StringValueExpression extends ValueExpression {

   /**
    * The value
    */
   public final String value;

   /**
    * Default Constructor
    *
    * @param value The value
    * @param type  The type of the value
    */
   public StringValueExpression(String value, ParserTokenType type) {
      super(type);
      this.value = value;
   }

   public StringValueExpression(ParserToken token) {
      super(token.type);
      this.value = token.text;
   }

   @Override
   public Object getValue() {
      return value;
   }

}//END OF ValueExpression
