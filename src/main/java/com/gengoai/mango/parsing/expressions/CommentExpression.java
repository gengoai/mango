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

package com.gengoai.mango.parsing.expressions;


import com.gengoai.mango.parsing.ParserTokenType;

/**
 * A comment expression.
 *
 * @author David B. Bracewell
 */
public class CommentExpression extends Expression {

  /**
   * The value of the comment
   */
  public final String comment;

  /**
   * Default Constructor
   *
   * @param type    The type of token that dominates the expression
   * @param comment The comment text
   */
  public CommentExpression(ParserTokenType type, String comment) {
    super(type);
    this.comment = comment;
  }

  @Override
  public String toString() {
    return comment;
  }


}//END OF CommentExpression
