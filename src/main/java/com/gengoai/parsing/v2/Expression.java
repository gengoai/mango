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

import com.gengoai.Tag;
import com.gengoai.conversion.Cast;

import java.io.Serializable;

/**
 * @author David B. Bracewell
 */
public class Expression implements Serializable {
   private final Tag type;

   public Expression(Tag type) {
      this.type = type;
   }

   public Tag getType() {
      return type;
   }

   public <T extends Expression> T as(Class<T> tClass) {
      if (tClass.isInstance(this)) {
         return Cast.as(this);
      }
      throw new IllegalStateException("Parse Exception: Attempting to convert an expression of type '" +
                                         getClass().getSimpleName() +
                                         "' to expression of type '" +
                                         tClass.getSimpleName()
      );
   }

}//END OF Expression