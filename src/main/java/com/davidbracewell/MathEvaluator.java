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
import com.davidbracewell.parsing.CommonTypes;
import com.davidbracewell.parsing.Evaluator;
import com.davidbracewell.parsing.Grammar;
import com.davidbracewell.parsing.ParseException;
import com.davidbracewell.parsing.Parser;
import com.davidbracewell.parsing.RegularExpressionLexer;
import com.davidbracewell.parsing.expressions.BinaryOperatorExpression;
import com.davidbracewell.parsing.expressions.Expression;
import com.davidbracewell.parsing.expressions.MethodCallExpression;
import com.davidbracewell.parsing.expressions.PrefixExpression;
import com.davidbracewell.parsing.expressions.ValueExpression;
import com.davidbracewell.parsing.handlers.BinaryOperatorHandler;
import com.davidbracewell.parsing.handlers.GroupHandler;
import com.davidbracewell.parsing.handlers.MethodCallHandler;
import com.davidbracewell.parsing.handlers.PrefixOperatorHandler;
import com.davidbracewell.parsing.handlers.ValueHandler;
import com.davidbracewell.reflection.Reflect;
import lombok.NonNull;

import java.text.NumberFormat;

/**
 * <p>Evaluates mathematical expressions in strings allowing using of all methods on {@link Math}.</p>
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


  private MathEvaluator() {
    throw new IllegalAccessError();
  }

  /**
   * Evaluates a mathematical expression contained in a string. All methods on {@link Math} are available as method
   * calls in the string.
   *
   * @param expression The expression to evaluate
   * @return The result of the evaluation
   * @throws ParseException Something went wrong during evaluation
   */
  public static double evaluate(@NonNull String expression) throws Exception {
    Parser parser = new Parser(MathGrammar.INSTANCE, lexer.lex(Resources.fromString(expression)));
    Expression next = parser.next();
    if (parser.hasNext()) {
      throw new ParseException("Invalid expression: " + expression);
    }
    return evaluator.eval(next);
  }

  private static class MathGrammar extends Grammar {
    private static final MathGrammar INSTANCE = new MathGrammar();

    private MathGrammar() {
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
  }


}//END OF MathEvaluator
