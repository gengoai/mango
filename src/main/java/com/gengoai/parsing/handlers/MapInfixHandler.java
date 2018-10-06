package com.gengoai.parsing.handlers;

import com.gengoai.parsing.ExpressionIterator;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.ParserToken;
import com.gengoai.parsing.ParserTokenType;
import com.gengoai.parsing.expressions.BinaryOperatorExpression;
import com.gengoai.parsing.expressions.Expression;
import com.gengoai.parsing.validation.ExpressionValidator;

/**
 * @author David B. Bracewell
 */
public class MapInfixHandler extends InfixHandler {
   private final MapPrefixHandler prefixHandler;

   public MapInfixHandler(int precedence,
                          ParserTokenType endOfMap,
                          ParserTokenType separator,
                          ExpressionValidator keyValueValidator
                         ) {
      super(precedence);
      prefixHandler = new MapPrefixHandler(endOfMap, separator, keyValueValidator);
   }

   @Override
   public Expression parse(ExpressionIterator expressionIterator, Expression left, ParserToken token) throws ParseException {
      return new BinaryOperatorExpression(left, token, prefixHandler.parse(expressionIterator, token));
   }
}//END OF MapHandler
