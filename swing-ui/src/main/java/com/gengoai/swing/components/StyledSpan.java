/*
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

package com.gengoai.swing.components;

import com.gengoai.collection.tree.SimpleSpan;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class StyledSpan extends SimpleSpan {
   public final String style;

   /**
    * Instantiates a new Simple span.
    *
    * @param start the start
    * @param end   the end
    * @param style
    */
   protected StyledSpan(int start, int end, String style) {
      super(start, end);
      this.style = style;
   }

   public String toString() {
      return String.format("StyledSpan{start=%d, end=%d, style='%s'}", start(), end(), style);
   }

}//END OF StyledSpan
