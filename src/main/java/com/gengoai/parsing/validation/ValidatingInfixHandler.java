package com.gengoai.parsing.validation;

import com.gengoai.parsing.ExpressionIterator;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.ParserToken;
import com.gengoai.parsing.expressions.Expression;
import com.gengoai.parsing.handlers.InfixHandler;

import static com.gengoai.Validation.notNull;

/**
 * @author David B. Bracewell
 */
public class ValidatingInfixHandler extends InfixHandler {
   private ExpressionValidator validator;
   private InfixHandler backing;

   protected ValidatingInfixHandler(InfixHandler backing,
                                    ExpressionValidator validator
                                   ) {
      super(backing.precedence());
      this.backing = backing;
      this.validator = validator;
   }


   public static InfixHandler infixValidator(InfixHandler backing,
                                             ExpressionValidator validator
                                            ) {
      return new ValidatingInfixHandler(notNull(backing), notNull(validator));
   }

   @Override
   public Expression parse(ExpressionIterator expressionIterator, Expression left, ParserToken token) throws ParseException {
      return validator.validate(backing.parse(expressionIterator, left, token));
   }

}//END OF ValidatingInfixHandler
