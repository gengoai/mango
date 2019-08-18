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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * The type List expression.
 *
 * @author David B. Bracewell
 */
public class ListExpression extends Expression implements Iterable<Expression> {
   private static final long serialVersionUID = 1L;
   private final List<Expression> expressions;

   /**
    * Instantiates a new List expression.
    *
    * @param type        the type
    * @param expressions the expressions
    */
   public ListExpression(Tag type, Collection<Expression> expressions) {
      super(type);
      this.expressions = new ArrayList<>(expressions);
   }

   /**
    * Handler prefix handler.
    *
    * @param operator  the operator
    * @param endOfList the end of list
    * @param separator the separator
    * @return the prefix handler
    */
   public static PrefixHandler handler(Tag operator, Tag endOfList, Tag separator) {
      return (p, t) -> new ListExpression(operator, p.parseExpressionList(endOfList, separator));
   }

   /**
    * Get expression.
    *
    * @param index the index
    * @return the expression
    */
   public Expression get(int index) {
      return expressions.get(index);
   }

   @Override
   public Iterator<Expression> iterator() {
      return expressions.iterator();
   }

   /**
    * Number of expressions int.
    *
    * @return the int
    */
   public int numberOfExpressions() {
      return expressions.size();
   }

   /**
    * Stream stream.
    *
    * @return the stream
    */
   public Stream<Expression> stream() {
      return expressions.stream();
   }

   @Override
   public String toString() {
      return "ListExpression{" +
                "type='" + getType() +
                "', expressions=" + expressions +
                '}';
   }
}//END OF ListExpression
