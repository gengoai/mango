package com.gengoai.parsing.expressions;

import com.gengoai.parsing.ParserTokenType;

import java.util.List;

/**
 * @author David B. Bracewell
 */
public class ArrayExpression extends Expression {
   public final List<Expression> expressions;

   /**
    * Default Constructor
    *
    * @param type The type of token that dominates the expression
    */
   public ArrayExpression(ParserTokenType type, List<Expression> expressions) {
      super(type);
      this.expressions = expressions;
   }

   @Override
   public String toString() {
      return expressions.toString();
   }

}//END OF ArrayExpression
