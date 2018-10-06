package com.gengoai.parsing.handlers;

import com.gengoai.parsing.ExpressionIterator;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.ParserToken;
import com.gengoai.parsing.ParserTokenType;
import com.gengoai.parsing.expressions.Expression;
import com.gengoai.parsing.expressions.MultivalueExpression;
import com.gengoai.parsing.validation.ExpressionValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David B. Bracewell
 */
public class MapPrefixHandler extends PrefixHandler {
   private final ParserTokenType endOfMap;
   private final ParserTokenType separator;
   private final ExpressionValidator keyValueValidator;

   public MapPrefixHandler(ParserTokenType endOfMap,
                           ParserTokenType separator,
                           ExpressionValidator keyValueValidator
                          ) {
      this.endOfMap = endOfMap;
      this.separator = separator;
      this.keyValueValidator = keyValueValidator == null ? e -> e : keyValueValidator;
   }

   @Override
   public Expression parse(ExpressionIterator expressionIterator, ParserToken token) throws ParseException {
      List<Expression> subExpressions = new ArrayList<>();
      boolean afterFirst = false;
      while (!expressionIterator.tokenStream().lookAheadType(0).isInstance(endOfMap)) {
         if (afterFirst && separator != null) {
            expressionIterator.tokenStream().consume(separator);
         }
         afterFirst = true;
         if (expressionIterator.tokenStream().lookAheadType(0).isInstance(endOfMap)) {
            throw new ParseException("Invalid trailing " + separator);
         }
         subExpressions.add(keyValueValidator.validate(expressionIterator.next()));
      }

      expressionIterator.tokenStream().consume(endOfMap);
      return new MultivalueExpression(subExpressions, token.getType());
   }
}//END OF MapHandler
