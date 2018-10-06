package com.gengoai.parsing.expressions;

import com.gengoai.parsing.ParserTokenType;

/**
 * @author David B. Bracewell
 */
public abstract class ValueExpression extends Expression {
   /**
    * Default Constructor
    *
    * @param type The type of token that dominates the expression
    */
   public ValueExpression(ParserTokenType type) {
      super(type);
   }

}//END OF ValueExpression
