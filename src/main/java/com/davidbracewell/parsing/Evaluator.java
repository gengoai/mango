package com.davidbracewell.parsing;

import com.davidbracewell.Switch;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.CheckedFunction;
import com.davidbracewell.parsing.expressions.Expression;
import lombok.NonNull;

/**
 * @author David B. Bracewell
 */
public abstract class Evaluator<O> extends Switch<Expression, O> {
  private static final long serialVersionUID = 1L;

  public Evaluator() {
    this.defaultStmt = exp -> {
      throw new ParseException("Unknown Expression [" + exp + " : " + exp.getTokenType() + "]");
    };
  }

  public O eval(Expression expression) throws Exception {
    return switchOn(expression);
  }

  protected final <E extends Expression> void $(Class<E> expressionClass, ParserTokenType type, @NonNull CheckedFunction<E, ? extends O> function) {
    $(
      e -> e.match(expressionClass, type),
      e -> Cast.as(e, expressionClass),
      function
    );
  }

  protected final <E extends Expression> void $(Class<E> expressionClass, @NonNull CheckedFunction<E, ? extends O> function) {
    $(
      e -> e.isInstance(expressionClass),
      e -> Cast.as(e, expressionClass),
      function
    );
  }

}// END OF Evaluator
