package com.gengoai.parsing.expressions;

import com.gengoai.parsing.ParserTokenType;

/**
 * @author David B. Bracewell
 */
public abstract class ValueExpression<T> extends Expression {
   /**
    * Default Constructor
    *
    * @param type The type of token that dominates the expression
    */
   public ValueExpression(ParserTokenType type) {
      super(type);
   }


   public abstract T getValue();

   @Override
   public String toString() {
      return String.valueOf(getValue());
   }
}//END OF ValueExpression
