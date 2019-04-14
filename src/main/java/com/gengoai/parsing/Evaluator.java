package com.gengoai.parsing;

import com.gengoai.Switch;
import com.gengoai.conversion.Cast;
import com.gengoai.function.CheckedFunction;
import com.gengoai.parsing.expressions.Expression;

/**
 * <p>An evaluator provides a switch-like interface for evaluating expressions. Custom evaluators can be created as
 * follows:
 * <pre>
 * {@code
 *
 * Evaluator<Double> eval = new Evaluator<Double>() {{
 *      $(BinaryOperatorExpression.class, CommonTypes.PLUS, e -> eval(e.left) + eval(e.right));
 *      $(ValueExpression.class, e -> Double.valueOf(e.toString()));
 * }}
 *
 * }
 * </pre>
 * The various <code>$</code> methods allow easily adding if-like predicates and then-like functions. The {@link
 * #eval(Expression)} method can be used to make recursive evaluation calls.
 * </p>
 *
 * @param <O> the type parameter
 * @author David B. Bracewell
 */
public abstract class Evaluator<O> extends Switch<Expression, O> {
   private static final long serialVersionUID = 1L;


   /**
    * Instantiates a new Evaluator.
    */
   protected Evaluator() {
      $default(exp -> {throw new ParseException("Unknown Expression [" + exp + " : " + exp.getTokenType() + "]");});
   }

   /**
    * Evaluates the given expression
    *
    * @param expression the expression to evaluate
    * @return the result of evaluation
    * @throws Exception Something went wrong during evaluation
    */
   public final O eval(Expression expression) throws Exception {
      return switchOn(expression);
   }

   /**
    * Adds a switch statement where the condition is that the expression is of type <code>expressionClass</code> and the
    * expressions's token type is an instance of <code>type</code>. When the condition is met the expression is cast as
    * the given expression class and the given function is applied.
    *
    * @param <E>             the type of expression
    * @param expressionClass the expression class
    * @param type            the token type
    * @param function        the function to apply when the condition is met.
    */
   protected final <E extends Expression> void $(Class<E> expressionClass, ParserTokenType type, CheckedFunction<E, ? extends O> function) {
      $case(e -> e.match(expressionClass, type),
            e -> Cast.as(e, expressionClass),
            function);
   }



   /**
    * Adds a switch statement where the condition is that the expression is of type <code>expressionClass</code>. When
    * the condition is met the expression is cast as the given expression class and the given function is applied.
    *
    * @param <E>             the type of expression
    * @param expressionClass the expression class
    * @param function        the function to apply when the condition is met.
    */
   protected final <E extends Expression> void $(Class<E> expressionClass, CheckedFunction<E, ? extends O> function) {
      $case(e -> e.isInstance(expressionClass),
            e -> Cast.as(e, expressionClass),
            function);
   }

}// END OF Evaluator
