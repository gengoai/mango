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

package com.gengoai.config;


import com.gengoai.parsing.expressions.Expression;

import java.util.List;

/**
 * The type Section expression.
 *
 * @author David B. Bracewell
 */
class SectionExpression extends Expression {

   /**
    * The Assignments.
    */
   public final List<Expression> assignments;
   /**
    * The Section prefix.
    */
   public final String sectionPrefix;

   /**
    * Default Constructor
    *
    * @param sectionPrefix the section prefix
    * @param assignments   the assignments
    */
   public SectionExpression(String sectionPrefix, List<Expression> assignments) {
      super(ConfigTokenizer.ConfigTokenType.SECTION_HEADER);
      this.sectionPrefix = sectionPrefix;
      this.assignments = assignments;
   }


}//END OF SectionExpression
