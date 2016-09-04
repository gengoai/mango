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

package com.davidbracewell;

import com.davidbracewell.io.Resources;
import com.davidbracewell.parsing.*;
import com.davidbracewell.parsing.expressions.*;
import com.davidbracewell.parsing.handlers.*;
import com.davidbracewell.reflection.Reflect;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;

/**
 * <p>Evaluates mathematical expressions in strings allowing using of all methods on {@link Math}. Serves as a simple
 * example of using the Mango parsing framework. In addition, a main method is defined that allows the class to be used
 * as a command line calculator.</p>
 *
 * @author David B. Bracewell
 */
public final class MathEvaluator {

   private static final RegularExpressionLexer lexer = RegularExpressionLexer.builder()
                                                                             .add(CommonTypes.OPENPARENS)
                                                                             .add(CommonTypes.CLOSEPARENS)
                                                                             .add(CommonTypes.NUMBER)
                                                                             .add(CommonTypes.PLUS)
                                                                             .add(CommonTypes.MINUS)
                                                                             .add(CommonTypes.MULTIPLY)
                                                                             .add(CommonTypes.DIVIDE)
                                                                             .add(CommonTypes.CARROT)
                                                                             .add(CommonTypes.COMMA)
                                                                             .add(CommonTypes.WORD, "[a-zA-z]\\w*")
                                                                             .build();

   private static final Evaluator<Double> evaluator = new Evaluator<Double>() {
      private static final long serialVersionUID = 1L;

      {
         $(ValueExpression.class, e -> NumberFormat.getInstance().parse(e.toString()).doubleValue());
         $(PrefixExpression.class, CommonTypes.MINUS, u -> -eval(u));
         $(BinaryOperatorExpression.class, CommonTypes.PLUS, e -> eval(e.left) + eval(e.right));
         $(BinaryOperatorExpression.class, CommonTypes.MINUS, e -> eval(e.left) - eval(e.right));
         $(BinaryOperatorExpression.class, CommonTypes.DIVIDE, e -> eval(e.left) / eval(e.right));
         $(BinaryOperatorExpression.class, CommonTypes.MULTIPLY, e -> eval(e.left) * eval(e.right));
         $(BinaryOperatorExpression.class, CommonTypes.CARROT, e -> Math.pow(eval(e.left), eval(e.right)));
         $(MethodCallExpression.class, method -> {
            Object[] args = new Object[method.arguments.size()];
            for (int i = 0; i < method.arguments.size(); i++) {
               args[i] = eval(method.arguments.get(i));
            }
            return Reflect.onClass(Math.class).invoke(method.methodName, args).get();
         });
      }
   };
   private static final Grammar grammar = new Grammar() {
      {
         register(CommonTypes.NUMBER, new ValueHandler());
         register(CommonTypes.OPENPARENS, new GroupHandler(CommonTypes.CLOSEPARENS));
         register(CommonTypes.PLUS, new BinaryOperatorHandler(3, true));
         register(CommonTypes.MINUS, new BinaryOperatorHandler(3, true));
         register(CommonTypes.MINUS, new PrefixOperatorHandler(12));
         register(CommonTypes.MULTIPLY, new BinaryOperatorHandler(6, true));
         register(CommonTypes.DIVIDE, new BinaryOperatorHandler(6, true));
         register(CommonTypes.CARROT, new BinaryOperatorHandler(10, true));
         register(CommonTypes.WORD, new ValueHandler());
         register(CommonTypes.OPENPARENS, new MethodCallHandler(12, CommonTypes.CLOSEPARENS, CommonTypes.COMMA));
      }
   };

   private MathEvaluator() {
      throw new IllegalAccessError();
   }

   /**
    * Evaluates a mathematical expression contained in a string. All methods on {@link Math} are available as method
    * calls in the string.
    *
    * @param expression The expression to evaluate
    * @return The result of the evaluation
    * @throws Exception the exception
    */
   public static double evaluate(@NonNull String expression) throws Exception {
      Parser parser = new Parser(grammar, lexer.lex(Resources.fromString(expression)));
      Expression next = parser.next();
      if (parser.hasNext()) {
         throw new ParseException("Invalid expression: " + expression);
      }
      return evaluator.eval(next);
   }

   /**
    * <p>Entry point to simple command line calculator application.</p>
    *
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
         String line;
         System.out.print("> ");
         while ((line = reader.readLine()) != null) {
            try {
               System.out.println(evaluate(line));
            } catch (Exception e) {
               e.printStackTrace();
            }
            System.out.print("> ");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }


}//END OF MathEvaluator
