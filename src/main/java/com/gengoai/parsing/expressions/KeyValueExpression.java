package com.gengoai.parsing.expressions;

import com.gengoai.parsing.ParserToken;

/**
 * A {@link ValueExpression} representing a Key in a key value pair
 *
 * @author David B. Bracewell
 */
public class KeyValueExpression extends ValueExpression<String> {
   /**
    * The Key.
    */
   public final String key;

   /**
    * Instantiates a new Key value expression.
    *
    * @param token the token
    */
   public KeyValueExpression(ParserToken token) {
      super(token.type);
      this.key = token.text;
   }


   @Override
   public String getValue() {
      return key;
   }

}//END OF KeyValueExpression
