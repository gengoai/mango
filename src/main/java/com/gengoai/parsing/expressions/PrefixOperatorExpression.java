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

/**
 * An <code>Expression</code> representing an prefix expression
 *
 * @author David B. Bracewell
 */
public class PrefixOperatorExpression extends Expression {
   /**
    * The operator
    */
   public final ParserToken operator;

   /**
    * The expression which the operator is operating on
    */
   public final Expression right;

   /**
    * Default Constructor
    *
    * @param operator The prefix operator
    * @param right    The expression on the right
    */
   public PrefixOperatorExpression(ParserToken operator, Expression right) {
      super(operator.type);
      this.operator = operator;
      this.right = right;
   }

   @Override
   public String toString() {
      return "(" + operator.text + right.toString() + ")";
   }

}//END OF PrefixOperatorExpression
