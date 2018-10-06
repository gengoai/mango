package com.gengoai.parsing.handlers;

import com.gengoai.parsing.ExpressionIterator;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.ParserToken;
import com.gengoai.parsing.ParserTokenType;
import com.gengoai.parsing.expressions.ArrayExpression;
import com.gengoai.parsing.expressions.Expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A handler to process an array of items with an optional separator between items.
 *
 * @author David B. Bracewell
 */
public class ArrayHandler extends PrefixHandler implements Serializable {
   private static final long serialVersionUID = 1L;
   private final ParserTokenType endOfArray;
   private final ParserTokenType separator;

   /**
    * Instantiates a new Array handler.
    *
    * @param endOfArray the end of array token type
    * @param separator  the separator token type (Optional)
    */
   public ArrayHandler(ParserTokenType endOfArray, ParserTokenType separator) {
      this.endOfArray = endOfArray;
      this.separator = separator;
   }

   @Override
   public Expression parse(ExpressionIterator expressionIterator, ParserToken token) throws ParseException {
      List<Expression> subExpressions = new ArrayList<>();
      while (!expressionIterator.tokenStream().lookAheadType(0).isInstance(endOfArray)) {
         Expression exp = expressionIterator.next();
         subExpressions.add(exp);
         if (!expressionIterator.tokenStream().lookAheadType(0).isInstance(endOfArray)) {
            if (separator != null) {
               expressionIterator.tokenStream().consume(separator);
            }
         }
      }
      expressionIterator.tokenStream().consume(endOfArray);
      return new ArrayExpression(token.type, subExpressions);
   }
}//END OF ArrayHandler
