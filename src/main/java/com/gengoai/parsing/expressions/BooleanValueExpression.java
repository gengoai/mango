package com.gengoai.parsing.expressions;

import com.gengoai.parsing.ParserToken;

/**
 * The type Boolean value expression.
 *
 * @author David B. Bracewell
 */
public class BooleanValueExpression extends ValueExpression<Boolean> {
   /**
    * The Value.
    */
   public final boolean value;

   /**
    * Instantiates a new Boolean value expression.
    *
    * @param token the token
    */
   public BooleanValueExpression(ParserToken token) {
      super(token.type);
      this.value = Boolean.valueOf(token.text);
   }

   @Override
   public Boolean getValue() {
      return value;
   }

}//END OF BooleanValueExpression
