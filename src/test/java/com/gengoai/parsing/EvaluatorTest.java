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

package com.gengoai.parsing;

import com.gengoai.collection.Lists;
import com.gengoai.parsing.expressions.BinaryOperatorExpression;
import com.gengoai.parsing.expressions.PostfixOperatorExpression;
import com.gengoai.parsing.expressions.ValueExpression;
import com.gengoai.parsing.handlers.BinaryOperatorHandler;
import com.gengoai.parsing.handlers.GroupHandler;
import com.gengoai.parsing.handlers.PostfixOperatorHandler;
import com.gengoai.parsing.handlers.ValueHandler;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class EvaluatorTest {
   Grammar grammar = new Grammar().registerSkip(CommonTypes.WHITESPACE) //Skip Whitespace, but nothing else
                                  .register(CommonTypes.PLUS, new BinaryOperatorHandler(10, false))
                                  .register(CommonTypes.MINUS, new BinaryOperatorHandler(10, false))
                                  .register(CommonTypes.MULTIPLY, new BinaryOperatorHandler(20, false))
                                  .register(CommonTypes.DIVIDE, new BinaryOperatorHandler(20, false))
                                  .register(CommonTypes.NUMBER, new ValueHandler())
                                  .register(CommonTypes.EXCLAMATION, new PostfixOperatorHandler(30))
                                  .register(CommonTypes.OPENPARENS, new GroupHandler(CommonTypes.CLOSEPARENS));

   Lexer lexer = RegularExpressionLexer.builder()
                                       .add(CommonTypes.WHITESPACE)
                                       .add(CommonTypes.NUMBER)
                                       .add(CommonTypes.PLUS)
                                       .add(CommonTypes.MINUS)
                                       .add(CommonTypes.MULTIPLY)
                                       .add(CommonTypes.DIVIDE)
                                       .add(CommonTypes.OPENPARENS)
                                       .add(CommonTypes.CLOSEPARENS)
                                       .add(CommonTypes.EXCLAMATION)
                                       .build();

   Evaluator<Double> mathEvaluator = new Evaluator<Double>() {
      private static final long serialVersionUID = 1L;

      {
         $(BinaryOperatorExpression.class, CommonTypes.PLUS, boe -> eval(boe.left) + eval(boe.right));
         $(BinaryOperatorExpression.class, CommonTypes.MINUS, boe -> eval(boe.left) - eval(boe.right));
         $(BinaryOperatorExpression.class, CommonTypes.MULTIPLY, boe -> eval(boe.left) * eval(boe.right));
         $(BinaryOperatorExpression.class, CommonTypes.DIVIDE, boe -> eval(boe.left) / eval(boe.right));
         $(PostfixOperatorExpression.class, CommonTypes.EXCLAMATION, pe -> eval(pe.left) * 10);
         $(ValueExpression.class, v -> Double.parseDouble(v.value));
      }
   };

   Parser parser = new Parser(grammar, lexer);


   @Test
   public void postfix() throws Exception {
      assertEquals(50, parser.evaluate("(2 + 3)!", mathEvaluator), 0);
      assertEquals(60, parser.evaluate("2 * 3!", mathEvaluator), 0);
   }

   @Test
   public void order() throws Exception {
      assertEquals(8, parser.evaluate("2+3 * 2", mathEvaluator), 0);
      assertEquals(10, parser.evaluate("(2 + 3) * 2", mathEvaluator), 0);
      assertEquals(5, parser.evaluate("(2 + 3) * 2 / 2", mathEvaluator), 0);
   }

   @Test(expected = ParseException.class)
   public void error() throws Exception {
      parser.evaluate("-2 * 3 + 5", mathEvaluator);
   }

   @Test
   public void all() throws Exception {
      assertEquals(Lists.list(8d), parser.evaluateAll("2+3 * 2", mathEvaluator));
   }

}//END OF EvaluatorTest
