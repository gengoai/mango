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

package com.gengoai.parsing;

import com.gengoai.Tag;

/**
 * An <code>Expression</code> unary operator which contains a single expression value
 *
 * @author David B. Bracewell
 */
public class UnaryOperatorExpression extends Expression {
   private static final long serialVersionUID = 1L;
   private final Expression value;

   /**
    * Instantiates a new Prefix operator expression.
    *
    * @param type  the type
    * @param value the value
    */
   public UnaryOperatorExpression(Tag type, Expression value) {
      super(type);
      this.value = value;
   }

   /**
    * Gets the {@link Expression} representing the value of the operator.
    *
    * @return the value
    */
   public Expression getValue() {
      return value;
   }

   /**
    * Generic Handler for generating {@link UnaryOperatorExpression}s for prefix operators using {@link
    * Parser#parseExpression()} to generate the value of the operator.
    */
   public static PrefixHandler PREFIX_OPERATOR_HANDLER = (p, t) -> new UnaryOperatorExpression(t.getType(),
                                                                                               p.parseExpression(t));

   /**
    * Generic Handler for generating {@link UnaryOperatorExpression}s for postfix operators.
    */
   public static PostfixHandler POSTFIX_OPERATOR_HANDLER = (parser, token, left) -> new UnaryOperatorExpression(
      token.getType(), left);
}//END OF PrefixOperatorExpression
