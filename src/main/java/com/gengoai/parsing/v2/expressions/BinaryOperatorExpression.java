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

package com.gengoai.parsing.v2.expressions;

import com.gengoai.parsing.v2.Expression;
import com.gengoai.parsing.v2.ParserToken;

/**
 * @author David B. Bracewell
 */
public class BinaryOperatorExpression extends Expression {
   private static final long serialVersionUID = 1L;
   private final Expression key;
   private final Expression value;
   private final String operator;

   public BinaryOperatorExpression(ParserToken token, Expression key, Expression right) {
      super(token.getType());
      this.operator = token.getText();
      this.key = key;
      this.value = right;
   }

   public Expression getKey() {
      return key;
   }

   public String getOperator() {
      return operator;
   }

   public Expression getValue() {
      return value;
   }

   @Override
   public String toString() {
      return String.format("%s %s %s", key, operator, value);
   }
}//END OF BinaryOperatorExpression
