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

import com.gengoai.Tag;
import com.gengoai.conversion.Val;
import com.gengoai.parsing.v2.Expression;
import com.gengoai.parsing.v2.PrefixHandler;

import java.util.Objects;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;

/**
 * @author David B. Bracewell
 */
public class ValueExpression extends Expression {
   private static final long serialVersionUID = 1L;
   public final Val value;

   public ValueExpression(Tag type, Object value) {
      super(type);
      this.value = Val.of(value);
   }

   public Val getValue() {
      return value;
   }

   @Override
   public int hashCode() {
      return Objects.hash(value, getType());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {return true;}
      if (obj == null || getClass() != obj.getClass()) {return false;}
      final ValueExpression other = (ValueExpression) obj;
      return Objects.equals(this.value, other.value) && Objects.equals(this.getType(), other.getType());
   }

   @Override
   public String toString() {
      return value.toString();
   }

   public static final PrefixHandler STRING_HANDLER = (p, t) -> new ValueExpression(t.getType(), t.getText());
   public static final PrefixHandler NUMERIC_HANDLER = (p, t) -> new ValueExpression(t.getType(),
                                                                                     parseDouble(t.getText()));
   public static final PrefixHandler BOOLEAN_HANDLER = (p, t) -> new ValueExpression(t.getType(),
                                                                                     parseBoolean(t.getText()));
}//END OF ValueExpression
