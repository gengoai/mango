package com.gengoai.parsing.expressions;

import com.gengoai.parsing.ParserToken;

/**
 * The type Null value expression.
 *
 * @author David B. Bracewell
 */
public class NullValueExpression extends ValueExpression {
   /**
    * Instantiates a new Null value expression.
    *
    * @param token the token
    */
   public NullValueExpression(ParserToken token) {
      super(token.type);
   }


   @Override
   public String toString() {
      return "null";
   }

}//END OF NullValueExpression
