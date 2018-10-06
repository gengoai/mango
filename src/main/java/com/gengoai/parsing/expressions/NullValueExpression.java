package com.gengoai.parsing.expressions;

import com.gengoai.parsing.ParserToken;

/**
 * The type Null value expression.
 *
 * @author David B. Bracewell
 */
public class NullValueExpression extends ValueExpression<Object> {
   /**
    * Instantiates a new Null value expression.
    *
    * @param token the token
    */
   public NullValueExpression(ParserToken token) {
      super(token.type);
   }

   @Override
   public Object getValue() {
      return null;
   }

}//END OF NullValueExpression
