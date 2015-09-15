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
import com.davidbracewell.reflection.ReflectionException;
import com.google.common.base.Throwables;
import lombok.NonNull;

import java.io.IOException;

/**
 * <p>Evaluates mathematical expressions in strings allowing using of all methods on {@link Math}.</p>
 *
 * @author David B. Bracewell
 */
public final class MathEvaluator {

  private MathEvaluator() {
    throw new IllegalAccessError();
  }

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

  private static double evaluate(Expression exp) throws ParseException {

    if (exp.isInstance(PrefixExpression.class)) {
      PrefixExpression unary = exp.as(PrefixExpression.class);
      if (CommonTypes.MINUS.isInstance(unary.getTokenType())) {
        return -evaluate(unary.right);
      }
      throw new ParseException("Unknown UnaryOperator [" + unary.operator.text + "]");
    }

    if (exp.isInstance(BinaryOperatorExpression.class)) {
      BinaryOperatorExpression binary = exp.as(BinaryOperatorExpression.class);
      if (CommonTypes.PLUS.isInstance(binary.operator.type)) {
        return evaluate(binary.left) + evaluate(binary.right);
      } else if (CommonTypes.MINUS.isInstance(binary.operator.type)) {
        return evaluate(binary.left) - evaluate(binary.right);
      } else if (CommonTypes.MULTIPLY.isInstance(binary.operator.type)) {
        return evaluate(binary.left) * evaluate(binary.right);
      } else if (CommonTypes.DIVIDE.isInstance(binary.operator.type)) {
        return evaluate(binary.left) / evaluate(binary.right);
      } else if (CommonTypes.CARROT.isInstance(binary.operator.type)) {
        return Math.pow(evaluate(binary.left), evaluate(binary.right));
      }
      throw new ParseException("Unknown BinaryOperator [" + binary.operator.text + "]");
    }

    if (exp.isInstance(ValueExpression.class)) {
      return Double.parseDouble(exp.toString().replaceAll("\\s*,\\s*", ""));
    }

    if (exp.isInstance(MethodCallExpression.class)) {
      MethodCallExpression method = exp.as(MethodCallExpression.class);
      Object[] args = new Object[method.arguments.size()];
      for (int i = 0; i < method.arguments.size(); i++) {
        args[i] = evaluate(method.arguments.get(i));
      }
      try {
        return Reflect.onClass(Math.class).invoke(method.methodName, args).get();
      } catch (ReflectionException e) {
        throw new ParseException(e);
      }
    }
    throw new ParseException("Unknown Expression [" + exp + " : " + exp.getTokenType() + "]");
  }

  /**
   * Evaluates a mathematical expression contained in a string. All methods on {@link Math} are available as method
   * calls in the string.
   *
   * @param expression The expression to evaluate
   * @return The result of the evaluation
   * @throws ParseException Something went wrong during evaluation
   */
  public static double evaluate(@NonNull String expression) throws ParseException {
    Parser parser;
    try {
      parser = new Parser(MathGrammar.INSTANCE, lexer.lex(Resources.fromString(expression)));
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
    Expression next = parser.next();
    if (parser.hasNext()) {
      throw new ParseException("Invalid expression: " + expression);
    }
    return evaluate(next);
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
